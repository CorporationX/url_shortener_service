package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
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
        String siteAddress = shortenerProperties.url().prefix();
        String url = "https://excalidraw.com/#json=aC2xh4e2J1DfKKiR2ZL1w,ELE95ANJQjpNZThWhPrpUQ";
        UrlRequestDto dto = UrlRequestDto.builder().url(url).build();
        UrlResponseDto resultDto = urlUtilService.shortenUrl(dto);

        Assertions.assertEquals(dto.url(), resultDto.url());
        Assertions.assertEquals(resultDto.shortUrl().length(), siteAddress.length() + resultDto.hash().length());
    }

    @Test
    @DisplayName("Test shortening incorrect url")
    void testShortenIncorrectUrl() {
        String url = "hhh.ru/test/sdfsdsdfsdfsdf/sdfsdfsdfsdf/sdfsdf";
        UrlRequestDto dto = UrlRequestDto.builder().url(url).build();
        UrlResponseDto resultDto = urlUtilService.shortenUrl(dto);

        Assertions.assertEquals(dto.url(), resultDto.url());
        Assertions.assertTrue(resultDto.shortUrl().length() < dto.url().length());
    }

}