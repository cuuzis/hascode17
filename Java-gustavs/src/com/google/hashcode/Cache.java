package com.google.hashcode;

import java.util.*;

public class Cache {
    public int memory;
    public List<Integer> videosStored = new ArrayList<>();
    public final HashMap<Integer, Endpoint> endpoints = new HashMap<>(); // key = endpointId
    public final HashMap<Integer, Integer> endpointLatencies = new HashMap<>(); // key = endpointId, value = latency
    public final HashMap<Integer, Integer> bestVideos = new HashMap<>(); // key = videoID, value = benefit (time saved)


    public Cache(int memory) {
        this.memory = memory;
    }

    public int putBestVideo() {
        int benefit = 0;
        if (bestVideos.entrySet().iterator().hasNext()) {
            int videoId = entriesSortedByValues(bestVideos).iterator().next().getKey(); // gets best video id
            if (Main.videoSizes[videoId] <= memory) {
                //System.out.println("put video " + videoId + ", saved: " + bestVideos.get(videoId));
                benefit = 0;//bestVideos.entrySet().iterator().next().getValue();
                bestVideos.remove(videoId);
                videosStored.add(videoId);
                memory -= Main.videoSizes[videoId];
                // updates connected endpoint video times
                for (Map.Entry<Integer, Endpoint> entry : endpoints.entrySet()) {
                    int endpointId = entry.getKey();
                    Endpoint endpoint = entry.getValue();
                    if (endpoint.videoRequests.containsKey(videoId)) {
                        int endpointToCacheLatency = endpointLatencies.get(endpointId);
                        Request endpointVideoRequest = endpoint.videoRequests.get(videoId);
                        if (endpointVideoRequest.videoLatency > endpointToCacheLatency) {
                            int endpointTimeSaved = endpointVideoRequest.videoLatency - endpointToCacheLatency;
                            endpointVideoRequest.videoLatency = endpointToCacheLatency;
                            //System.out.println("endpoint " + endpointId + " video " + videoId + ", saved time: " + endpointTimeSaved * endpointVideoRequest.requestTimes);
                            benefit += endpointTimeSaved * endpointVideoRequest.requestTimes;
                            // updates best video benefits of endpoint connected caches
                            endpoint.caches.forEach((cacheId, cache) -> {
                            /*for (Map.Entry<Integer, Cache> cEntry : endpoint.caches.entrySet()) {
                                int cacheId = cEntry.getKey();
                                Cache cache = cEntry.getValue();*/
                                if (cache.bestVideos.containsKey(videoId)) {
                                    int cacheBenefit = cache.bestVideos.get(videoId);
                                    cacheBenefit -= (endpointTimeSaved * endpointVideoRequest.requestTimes);
                                    if (cacheBenefit <= 0)
                                        cache.bestVideos.remove(videoId);
                                    else
                                        cache.bestVideos.put(videoId, cacheBenefit);
                                }
                            });
                            // endpoint time saved = ...
                            // endpoint.caches -> if has best video, then best video benefit -= endpoint time saved
                        }
                    }
                };
            }

        }
        return benefit;
    }

    public void calculateBenefits() {
        for (Map.Entry<Integer, Endpoint> entry : endpoints.entrySet()) {
            int endpointId = entry.getKey();
            Endpoint endpoint = entry.getValue();
            endpoint.videoRequests.forEach((videoId, request) -> {
                int timeSaved = (request.videoLatency - this.endpointLatencies.get(endpointId)) * request.requestTimes;
                int videoSize = Main.videoSizes[videoId];
                if (timeSaved > 0 && videoSize < memory) {
                    if (bestVideos.containsKey(videoId))
                        timeSaved += bestVideos.get(videoId);
                    bestVideos.put(videoId, timeSaved);
                }
            });
        }
    }

    // http://stackoverflow.com/questions/11647889/sorting-the-mapkey-value-in-descending-order-based-on-the-value
    // sorts best videos by time saved (ignores memory)
    static <K,V extends Comparable<? super V>>
    List<Map.Entry<K, V>> entriesSortedByValues(Map<K,V> map) {
        List<Map.Entry<K,V>> sortedEntries = new ArrayList<Map.Entry<K,V>>(map.entrySet());
        Collections.sort(sortedEntries,
                (e1, e2) -> e2.getValue().compareTo(e1.getValue())
        );
        return sortedEntries;
    }
}
