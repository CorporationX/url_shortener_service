package faang.school.urlshortenerservice.service.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UrlControllerTest {

    @Value(value = "${local.server.port}")
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    String apiUrl = "http://localhost:" + port + "/url";

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:13.3"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    public void testCreateShortLink() {
        UrlDto urlDto = new UrlDto("http://example123.com");
        String apiUrl = "http://localhost:" + port + "/url";
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, urlDto, String.class);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getOriginLink() {
        String apiUrl = "http://localhost:" + port + "/url";
        UrlDto urlDto = new UrlDto("http://example123.com");
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, urlDto, String.class);
        String shortUrl = responseEntity.getBody();
        ResponseEntity<Void> redirectResponse = restTemplate.getForEntity(apiUrl + "/" + shortUrl, Void.class);
        HttpStatusCode statusCode = redirectResponse.getStatusCode();
    }
}
