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

            //Process
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


            // Calculations
            // 1. for each cache, find best video to store (weight = videoRequests * saved ms / additional space % used )
            // -- for each connected endpoint, for each video
            // --- see current video play speed
            // --- calculate benefit if played from cache
            // --- add benefit to list in cache
            //
            // 2. for each cache, while cache is not full:
            // add best video to stored in cache
            // update video play speed in connected endpoints
            // go to next cache


            //calcualate initial video "benefit" potential
            caches.forEach((cacheId, cache) -> {
                cache.calculateBenefits();
            });


            while (true) {
                long savedTimeBefore = savedTime;
                caches.forEach((cacheId, cache) -> {
                    //System.out.println("Cache " + cacheId);
                    savedTime += cache.putBestVideo();
                });
                if (savedTimeBefore == savedTime)
                    break;
            }


            //put videos, one for each cache
            /*while (true) {
                int savedTimeBefore = savedTime;
                for (Map.Entry<Integer, Cache> entry : caches.entrySet()) {
                    int cacheId = entry.getKey();
                    Cache cache = entry.getValue();
                    savedTime += cache.putBestVideo();
                }
                if (savedTimeBefore == savedTime)
                    break;
            }*/

            /*caches.forEach((cacheId, cache) -> {
                int[] result = getBestVideo(cache);
                int videoId = result[0];
                int savedMilis = result[1];
                savedTime += savedMilis;

                cache.putVideo(videoId);
            });*/



            /*for (Map.Entry<Integer, Cache> entry : caches.entrySet()) {
                int key = entry.getKey();
                Cache value = entry.getValue();
            }*/

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
                System.out.println("Predicted result: " + (savedTime * 1000 / totalRequests) );
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
