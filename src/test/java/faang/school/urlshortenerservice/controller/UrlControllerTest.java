package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.BaseIntegrationTest;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.HashGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UrlControllerTest extends BaseIntegrationTest {

    @Autowired
    private RedisTemplate<String, String> hashRedisTemplate;
    @Autowired
    private HashGenerator hashGenerator;

    @Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/insert-urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void redirectToUrl() {
        webTestClient.get()
                .uri("/shortner/550e8")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/test/url/550e8");
    }

    @Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void redirectToUrlCacheTest() {
        hashRedisTemplate.opsForValue().set("550e8", "/test/url/550e8");

        webTestClient.get()
                .uri("/shortner/550e8")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/test/url/550e8");

        hashRedisTemplate.delete("550e8");
    }

    @Test
    void redirectToUrlNotFound() {
        webTestClient.get()
                .uri("/shortner/550e8")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void createShortUrl() {
        hashGenerator.checkHashCountsAsync();
        UrlDto urlDto = UrlDto.builder()
                .url("http://www.google.com")
                .build();

        String shorUrl = webTestClient.post()
                .uri("/shortner/url")
                .bodyValue(urlDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String expectedUrl = "http://localhost:8080/shortner";
        Assertions.assertTrue(shorUrl.startsWith(expectedUrl));
    }

    @Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/insert-hash.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void createShortUrlFailure() {
        UrlDto urlDto = UrlDto.builder()
                .url("htp://www.google.com")
                .build();

        webTestClient.post()
                .uri("/shortner/url")
                .bodyValue(urlDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @AfterEach
    void clearRedis() {
        hashRedisTemplate.delete("*");
    }
}