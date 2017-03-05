package com.google.hashcode;

import java.util.HashMap;

public class Endpoint {
    public final int dataCenterLatency;
    public final HashMap<Integer, Cache> caches = new HashMap<>(); // key = cacheId
    public final HashMap<Integer, Integer> cacheLatencies = new HashMap<>(); // key = cacheId
    public final HashMap<Integer, Request> videoRequests = new HashMap<>(); // key = videoId


    public Endpoint(int dataCenterLatency) {
        this.dataCenterLatency = dataCenterLatency;
    }
}
