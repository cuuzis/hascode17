package com.google.hashcode;

public class Request {
    public int videoLatency;
    public final int requestTimes;

    public Request(int requestLatency, int requestTimes) {
        this.videoLatency = requestLatency;
        this.requestTimes = requestTimes;
    }
}
