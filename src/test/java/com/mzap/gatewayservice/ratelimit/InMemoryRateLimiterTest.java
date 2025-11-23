package com.mzap.gatewayservice.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRateLimiterTest {

    private InMemoryRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new InMemoryRateLimiter();
        ReflectionTestUtils.setField(rateLimiter, "window", 200L);
        ReflectionTestUtils.setField(rateLimiter, "maxRequests", 3);
    }

    @Test
    void allowsUpToMaxRequestsThenBlocks() {
        String key = "client-1";
        assertTrue(rateLimiter.isAllowed(key));
        assertTrue(rateLimiter.isAllowed(key));
        assertTrue(rateLimiter.isAllowed(key));
        assertFalse(rateLimiter.isAllowed(key), "Should block after reaching maxRequests in the same window");
    }

    @Test
    void remainingCallsReflectsUsage() {
        String key = "client-2";
        assertEquals(3, rateLimiter.remainingCalls(key));
        assertTrue(rateLimiter.isAllowed(key));
        assertEquals(2, rateLimiter.remainingCalls(key));
        assertTrue(rateLimiter.isAllowed(key));
        assertEquals(1, rateLimiter.remainingCalls(key));
    }

    @Test
    void afterWindowResetsRequestsAreAllowedAgain() throws InterruptedException {
        String key = "client-3";
        assertTrue(rateLimiter.isAllowed(key));
        assertTrue(rateLimiter.isAllowed(key));
        assertTrue(rateLimiter.isAllowed(key));
        assertFalse(rateLimiter.isAllowed(key));

        // Wait for the window to roll over
        Thread.sleep(250L);

        assertTrue(rateLimiter.isAllowed(key));
        assertTrue(rateLimiter.isAllowed(key));
        assertTrue(rateLimiter.isAllowed(key));
        assertFalse(rateLimiter.isAllowed(key));
    }
}
