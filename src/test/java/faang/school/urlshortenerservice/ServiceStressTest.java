package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.controller.UrlShortenerController;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@Testcontainers
class ServiceStressTest {

    @Autowired
    private UrlShortenerController urlShortenerController;
    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:13")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    @DisplayName("Test creation many urls")
    void testStressCreationUrls() {
        int start = 6000;
        int quantity = 2000;

        UrlRequestDto urlRequestDto;
        UrlResponseDto urlResponseDto;
        String sitePrefix = "http://somesite.ru/long/url/with/some/params?param=value&id=";
        List<UrlResponseDto> generatedResponses = new ArrayList<>();

        for (int i = start; i < start + quantity; i++) {
            urlRequestDto = UrlRequestDto.builder().url(sitePrefix + i).build();
            urlResponseDto = urlShortenerController.createShortUrl(urlRequestDto);
            generatedResponses.add(urlResponseDto);
            Assertions.assertEquals(urlRequestDto.url(), urlResponseDto.url());
            System.out.println("ITERATION " + i);
        }
        Assertions.assertEquals(quantity, generatedResponses.size());
    }

    @Test
    @DisplayName("Test getting many urls")
    void testStressGettingUrls() {
        int quantity = 2000;

        UrlRequestDto urlRequestDto;
        UrlResponseDto urlResponseDto;
        String site = "http://somesite.ru/long/url/with/some/params?param=value&id=1234567890";
        urlRequestDto = UrlRequestDto.builder().url(site).build();
        urlResponseDto = urlShortenerController.createShortUrl(urlRequestDto);
        String hash = urlResponseDto.hash();

        for (int i = 0; i < quantity; i++) {
            ResponseEntity<Void> result = urlShortenerController.redirectToUrl(hash);
            HttpStatusCode httpStatusCode = result.getStatusCode();
            Assertions.assertEquals("302 FOUND", httpStatusCode.toString());
            String location = Objects.requireNonNull(result.getHeaders().get("Location")).get(0);
            Assertions.assertEquals(site, location);
            System.out.println("ITERATION " + i);
        }
    }


}
