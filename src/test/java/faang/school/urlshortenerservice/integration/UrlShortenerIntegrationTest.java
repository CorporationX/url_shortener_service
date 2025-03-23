package faang.school.urlshortenerservice.integration;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URL;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@AutoConfigureWebTestClient
@Testcontainers
public class UrlShortenerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UrlRepository urlRepository;

    @Test
    public void testUrlEndToEndFlow() throws Exception {
        UrlDto requestDto = new UrlDto();
        requestDto.setUrl("http://example.com/long-url");

        URL shortUrl = webTestClient.post()
                .uri("/url")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(URL.class)
                .returnResult()
                .getResponseBody();

        assertThat(shortUrl).isNotNull();
        String shortUrlStr = shortUrl.toString();
        String hash = extractHash(shortUrlStr);
        assertThat(hash).isNotEmpty();
        assertThat(hash.length()).isEqualTo(6);

        webTestClient.get()
                .uri("/url/{hash}", hash)
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals("Location", requestDto.getUrl());

        Url storedUrl = urlRepository.findById(hash).orElse(null);
        assertThat(storedUrl).isNotNull();
        assertThat(storedUrl.getUrl()).isEqualTo(requestDto.getUrl());
        assertThat(storedUrl.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    private String extractHash(String shortUrl) {
        String[] parts = shortUrl.split("/");
        return parts[parts.length - 1];
    }
}
