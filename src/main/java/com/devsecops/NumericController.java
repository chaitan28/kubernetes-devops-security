
package com.devsecops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class NumericController {

    private static final Logger logger =
            LoggerFactory.getLogger(NumericController.class);

    private final RestTemplate restTemplate;

    @Value("${node.service.base-url:http://node-service:5000/plusone}")
    private String baseURL;

    public NumericController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String welcome() {
        return "Kubernetes DevSecOps";
    }

    @GetMapping("/compare/{value}")
    public String compareToFifty(@PathVariable int value) {

        if (value > 50) {
            return "Greater than 50";
        }

        return "Smaller than or equal to 50";
    }

    @GetMapping("/increment/{value}")
    public int increment(@PathVariable int value) {

        String url = baseURL + "/" + value;

        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(url, String.class);

        String response = responseEntity.getBody();

        logger.info("Value received in request: {}", value);
        logger.info("Node service response: {}", response);

        if (response == null) {
            throw new IllegalStateException(
                    "Node service returned an empty response");
        }

        return Integer.parseInt(response.trim());
    }
}
