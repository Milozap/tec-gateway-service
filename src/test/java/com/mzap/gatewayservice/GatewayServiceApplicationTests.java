package com.mzap.gatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class GatewayServiceApplicationTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> GatewayServiceApplication.main(new String[]{}));
    }

}
