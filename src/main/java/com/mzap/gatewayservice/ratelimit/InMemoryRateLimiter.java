package com.mzap.gatewayservice.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class InMemoryRateLimiter {

    @Value("${gateway.rateLimit.window}")
    private long window;
    @Value("${gateway.rateLimit.maxRequests}")
    private int maxRequests;

    private static class Counter {
        long windowStart;
        int count;

        public Counter(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    public boolean isAllowed(String key) {
        long now = Instant.now().toEpochMilli();

        Counter counter = counters.computeIfAbsent(key, _ -> new Counter(now, 0));

        synchronized (counter) {
            if(now - counter.windowStart > window) {
                counter.windowStart = now;
                counter.count = 0;
            }

            if(counter.count >= maxRequests) {
                return false;
            }

            counter.count++;
            return true;
        }
    }

    public int remainingCalls(String key) {
        Counter counter = counters.get(key);
        return maxRequests - (counter == null ? 0 : counter.count);
    }
}
