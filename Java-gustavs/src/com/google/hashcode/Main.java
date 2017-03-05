package com.google.hashcode;

import java.io.*;
import java.util.*;

public class Main {
    private static Map<Integer, Endpoint> endpoints;
    private static Map<Integer, Cache> caches;
    public static int[] videoSizes;
    public static long savedTime;
    public static long totalRequests;

    public static void main(String[] args)
    {
        //doWork("example");
        doWork("me_at_the_zoo");
        doWork("videos_worth_spreading");
        doWork("trending_today");
        doWork("kittens");
    }

    static void doWork(String filename) {
        endpoints = new HashMap<>();
        caches = new HashMap<>();
        savedTime = 0;
        totalRequests = 0;

        File infile = new File(filename);
        try (FileReader fileReader = new FileReader(infile + ".in")) {
            BufferedReader br = new BufferedReader(fileReader);
            String fileLine;
            fileLine = br.readLine();
            String[] nums = fileLine.split(" ");
            final int videoCount = Integer.parseInt(nums[0]);
            final int endpointCount = Integer.parseInt(nums[1]);
            final int requestCount = Integer.parseInt(nums[2]);
            final int cacheCount = Integer.parseInt(nums[3]);
            final int cacheSize = Integer.parseInt(nums[4]);

            for (int cacheId = 0; cacheId < cacheCount; cacheId++)
                caches.put(cacheId, new Cache(cacheSize));

            // videoSizes[id] = size (MB)
            videoSizes = new int[videoCount];
            fileLine = br.readLine();
            String[] videodata = fileLine.split(" ");
            for (int videoId = 0; videoId < videoCount; videoId++) {
                videoSizes[videoId] = Integer.parseInt(videodata[videoId]);
            }

            for(int endpointId = 0; endpointId < endpointCount; endpointId++) {
                fileLine = br.readLine();
                String[] endpdata = fileLine.split(" ");
                int endpointDataCenterLatency = Integer.parseInt(endpdata[0]);
                int endpointCaches = Integer.parseInt(endpdata[1]);

                Endpoint endpoint = new Endpoint(endpointDataCenterLatency);
                for (int j = 0; j < endpointCaches; j++) {
                    fileLine = br.readLine();
                    String[] cacheData = fileLine.split(" ");
                    int endpointCacheId = Integer.parseInt(cacheData[0]);
                    int endpointCacheLatency = Integer.parseInt(cacheData[1]);

                    Cache endpCache = caches.get(endpointCacheId);
                    endpCache.endpoints.put(endpointId, endpoint);
                    endpCache.endpointLatencies.put(endpointId, endpointCacheLatency);
                    endpoint.caches.put(endpointCacheId, endpCache);
                    endpoint.cacheLatencies.put(endpointCacheId, endpointCacheLatency);
                }
                endpoints.put(endpointId, endpoint);
            }

            for(int requestId = 0; requestId < requestCount; requestId++) {
                fileLine = br.readLine();
                String[] reqdata = fileLine.split(" ");
                int requestVideoId = Integer.parseInt(reqdata[0]);
                int requestEndpointId = Integer.parseInt(reqdata[1]);
                int requestTimes = Integer.parseInt(reqdata[2]);

                Endpoint endpoint = endpoints.get(requestEndpointId);
                int requestLatency = endpoint.dataCenterLatency;
                endpoint.videoRequests.put(requestVideoId, new Request(requestLatency, requestTimes));
                totalRequests += requestTimes;
            }

            // input check
            /*System.out.println("Video sizes (MB)");
            System.out.println(Arrays.toString(videoSizes));
            System.out.println("Endpoints (id: data center latency)");
            endpoints.forEach((endpointId, endpoint) -> {
                System.out.println(endpointId + ": " + endpoint.dataCenterLatency);
                System.out.println("  connected caches (cache id: size (MB), latency to endpoint)");
                endpoint.caches.forEach((cacheId, cache) -> {
                    System.out.println("  " + cacheId + ": " + cache.memory + " " + endpoint.cacheLatencies.get(cacheId));
                });
                System.out.println("  video requests (video id: request times)");
                endpoint.videoRequests.forEach((videoId, request) -> {
                    System.out.println("  " + videoId + ": " + request.requestTimes);
                });
            });
            System.out.println("Caches (id: memory (MB))");
            caches.forEach((cacheId, cache) -> {
                System.out.println(cacheId + ": " + cache.memory);
                System.out.println("  connected endpoints");
                cache.endpoints.forEach((endpointId, endpoint) -> {
                    System.out.println("  " + endpointId + ": " + endpoint.dataCenterLatency + " " + cache.endpointLatencies.get(endpointId));
                });
            });*/


            // Computation

            // A) for each cache:
            //  get all videos requested by endpoints connected to it
            //  calculate benefit for storing each single video and save it in cache.bestVideos
            //  benefit = videoRequests * saved ms
            //  for getting the best video later, (videoRequests * saved ms / video size) is used
            caches.forEach((cacheId, cache) -> {
                cache.calculateBenefits();
            });

            // B) fill caches:
            //  take 1st cache,
            //  put the video with the best benefit
            //    for each endpoint that requests this video and is connected to this cache:
            //      update this video's request latency
            //      update this video's storage benefit for all other caches connected to the endpoint
            //  go to next cache
            //  put the video with the best benefit
            //  ..
            //  repeat until all caches are full
            while (true) {
                long savedTimeBefore = savedTime;
                caches.forEach((cacheId, cache) -> {
                    //System.out.println("Cache " + cacheId);
                    savedTime += cache.putBestVideo();
                });
                if (savedTimeBefore == savedTime)
                    break;
            }

            // save results
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename + ".out"))) {
                int cachesUsed = 0;
                for (Cache cache: caches.values()) {
                    if (cache.videosStored.size() > 0)
                        cachesUsed++;
                }
                bw.write(cachesUsed + "\n");

                for (Map.Entry<Integer, Cache> entry : caches.entrySet()) {
                    int cacheId = entry.getKey();
                    Cache cache = entry.getValue();
                    if (cache.videosStored.size() > 0) {
                        bw.write(Integer.toString(cacheId));
                        for (int videoId: cache.videosStored) {
                            bw.write(" " + videoId);
                        }
                        bw.write("\n");
                    }
                }
                System.out.println("Saved " + cachesUsed + " caches to " + filename + ".out");
                System.out.println("Predicted result: " + (savedTime * 1000 / totalRequests) ); // way off from google's results
                System.out.println("Total requests: " + totalRequests);
            } catch (IOException e) {
                System.out.println("Write error!");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Read error!");
            e.printStackTrace();
        }
    }
}
