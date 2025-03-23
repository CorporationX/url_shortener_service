package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class UrlShortenerControllerTest {

    @Autowired
    private UrlService urlService;
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

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Test URL validate")
    void testCreateShortUrl() {
        UrlRequestDto emptyUrlRequestDto = UrlRequestDto.builder()
                .url("")
                .build();
        UrlRequestDto incorrectUrlRequestDto1 = UrlRequestDto.builder()
                .url("ftp:/234")
                .build();
        UrlRequestDto incorrectUrlRequestDto2 = UrlRequestDto.builder()
                .url("sdfgsdfgsdf")
                .build();
        UrlRequestDto urlRequestDto = UrlRequestDto.builder()
                .url("http://ya.ru/")
                .build();
        UrlResponseDto resultDto;

        resultDto = urlShortenerController.createShortUrl(urlRequestDto);
        Assertions.assertNotEquals(null, resultDto.shortUrl());
        resultDto = urlShortenerController.createShortUrl(emptyUrlRequestDto);
        Assertions.assertNotEquals(null, resultDto.shortUrl());
        resultDto = urlShortenerController.createShortUrl(incorrectUrlRequestDto1);
        Assertions.assertNotEquals(null, resultDto.shortUrl());
        //TODO пока не получается некорректные url проверить в тесте
        resultDto = urlShortenerController.createShortUrl(incorrectUrlRequestDto2);
        Assertions.assertNotEquals(null, resultDto.shortUrl());

    }
}