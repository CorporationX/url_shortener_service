package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import org.junit.Assert;
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
class UrlUtilServiceTest {

    @Autowired
    private UrlService urlService;
    @Autowired
    private ShortenerProperties shortenerProperties;
    @Autowired
    private UrlUtilService urlUtilService;

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
    @DisplayName("Test shortening url")
    void testShortenUrl() {
        String url = "https://excalidraw.com/#json=aC2xh4e2J1DfKKiR2ZL1w,ELE95ANJQjpNZThWhPrpUQ";
        UrlRequestDto dto = UrlRequestDto.builder().url(url).build();
        UrlResponseDto resultDto = urlUtilService.shortenUrl(dto);

        Assert.assertEquals(dto.url(), resultDto.url());
        Assert.assertTrue(resultDto.shortUrl().length() < dto.url().length());
    }

    @Test
    @DisplayName("Test getting url")
    void getFullUrl() {
        // TODO доделать тест
/*
        String url = "https://excalidraw.com/#json=aC2xh4e2J1DfKKiR2ZL1w,ELE95ANJQjpNZThWhPrpUQ";
        UrlRequestDto dto = UrlRequestDto.builder().url(url).build();
        UrlResponseDto resultDto = urlUtilService.shortenUrl(dto);

        UrlRequestDto requestDto = UrlRequestDto.builder().shortUrl(resultDto.shortUrl()).build();
        UrlResponseDto responseDto = urlUtilService.getFullUrl(requestDto);

        Assert.assertEquals(url, responseDto.url());*/
    }
}