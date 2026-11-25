
package com.devsecops;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NumericApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private RestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void testWelcome() {

        String response = testRestTemplate.getForObject(
                getBaseUrl() + "/", String.class);

        assertEquals("Kubernetes DevSecOps", response);
    }

    @Test
    void testCompareGreaterThanFifty() {

        String response = testRestTemplate.getForObject(
                getBaseUrl() + "/compare/75", String.class);

        assertEquals("Greater than 50", response);
    }

    @Test
    void testCompareLessThanFifty() {

        String response = testRestTemplate.getForObject(
                getBaseUrl() + "/compare/25", String.class);

        assertEquals("Smaller than or equal to 50", response);
    }

    @Test
    void testCompareExactlyFifty() {

        String response = testRestTemplate.getForObject(
                getBaseUrl() + "/compare/50", String.class);

        assertEquals("Smaller than or equal to 50", response);
    }

    @Test
    void testIncrement() {

        when(restTemplate.getForEntity(
                "http://node-service:5000/plusone/10",
                String.class))
                .thenReturn(ResponseEntity.ok("11"));

        Integer response = testRestTemplate.getForObject(
                getBaseUrl() + "/increment/10", Integer.class);

        assertEquals(11, response);

        verify(restTemplate, times(1)).getForEntity(
                "http://node-service:5000/plusone/10",
                String.class);
    }

    @Test
    void testIncrementWithDifferentValue() {

        when(restTemplate.getForEntity(
                "http://node-service:5000/plusone/20",
                String.class))
                .thenReturn(ResponseEntity.ok("21"));

        Integer response = testRestTemplate.getForObject(
                getBaseUrl() + "/increment/20", Integer.class);

        assertEquals(21, response);
    }
}
