package com.reliaquest.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ApiApplicationTest {

    @Test
    void someTest() {
        assertDoesNotThrow(() -> ApiApplication.main(new String[] {}));
    }
}
