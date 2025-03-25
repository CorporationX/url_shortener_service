package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.error.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.cache.HashLocalCache;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

@SpringBootTest
@Testcontainers
@Transactional
class UrlServiceImplTest {

    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private HashLocalCache hashLocalCache;
    @Autowired
    private UrlMapper urlMapper;
    @Autowired
    private ShortenerProperties shortenerProperties;
    @Autowired
    private UrlService urlService;

    Url url1;
    Url url2;
    UrlResponseDto emptyUrlResponseDto = UrlResponseDto.builder().build();

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
    @DisplayName("Test get or create url")
    void testGetOrCreateUrl() {
        String urlAddress1 =  "http://test11231231231.ru/";
        UrlResponseDto urlDto = urlService.getOrCreateUrl(urlAddress1);

        Assertions.assertEquals("NX2KAG",urlDto.hash());
        //Assertions.assertEquals("NX2LAG", urlDto.hash());
        Assertions.assertEquals(urlAddress1, urlDto.url());
        Assertions.assertEquals("http://site.com/NX2KAG",urlDto.shortUrl());
        //Assertions.assertEquals("http://site.com/NX2LAG", urlDto.shortUrl());
        UrlResponseDto urlDtoFromDb = urlService.getOrCreateUrl(urlAddress1);
        Assertions.assertEquals(urlAddress1, urlDtoFromDb.url());
    }

    @Test
    @DisplayName("Test get url by hash")
    void testGetUrlByHash() {

        url1 = new Url("h11111", "http://test1.ru/", LocalDateTime.now().plusDays(1));
        url2 = new Url("h22222", "http://test2.ru/", LocalDateTime.now().plusDays(10));
        urlRepository.save(url1);
        urlRepository.save(url2);

        String hash1 =  "h11111";
        UrlResponseDto testedUrl1 = urlService.getUrlByHash(hash1);
        Assertions.assertEquals(hash1, testedUrl1.hash());

        String hash2 =  "h22222";
        UrlResponseDto testedUrl2= urlService.getUrlByHash(hash2);
        Assertions.assertEquals(hash2, testedUrl2.hash());

        String hash3 =  "notExists";
        //UrlResponseDto testedUrl3 = urlService.getUrlByHash(hash3);
        //Assertions.assertNull(testedUrl3.hash());

        UrlNotFoundException exception = Assertions.assertThrows(
                UrlNotFoundException.class,
                () -> urlService.getUrlByHash(hash3)
        );

        Assertions.assertEquals("Url not found, hash: notExists", exception.getMessage());

    }
}