package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.LocalHashCache;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.Assert;
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
    private LocalHashCache localHashCache;
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
        url1 = Url.builder()
                .hash("h11111")
                .url("http://test1.ru/")
                .expiredAtDate(LocalDateTime.now().plusDays(1))
                .build();
        url2 = Url.builder()
                .hash("h22222")
                .url("http://test2.ru/")
                .expiredAtDate(LocalDateTime.now().plusDays(10))
                .build();
        urlRepository.save(url1);
        urlRepository.save(url2);
    }

    @Test
    @DisplayName("Test create cached url")
    void testCreateCachedUrl() {
        String urlAddress1 =  "http://test1.ru/";
        UrlResponseDto urlDto = urlService.createCachedUrl(urlAddress1);

        Assert.assertEquals("NX2KAG",urlDto.hash());
        Assert.assertEquals(urlAddress1,urlDto.url());
        Assert.assertEquals("http://site.com/NX2KAG",urlDto.shortUrl());

        UrlResponseDto urlDtoFromDb = urlService.getUrl(urlAddress1);
        Assert.assertEquals(urlAddress1, urlDtoFromDb.url());

    }

    @Test
    @DisplayName("Test get url")
    void testGetUrl() {

        String urlAddress1 =  "http://test1.ru/";
        UrlResponseDto testedUrl1 = urlService.getUrl(urlAddress1);
        Assert.assertEquals(urlAddress1, testedUrl1.url());

        String urlAddress2 =  "http://test2.ru/";
        UrlResponseDto testedUrl2 = urlService.getUrl(urlAddress2);
        Assert.assertEquals(urlAddress2, testedUrl2.url());

        String urlAddress3 =  "http://notexists.ru/";
        UrlResponseDto testedUrl3 = urlService.getUrl(urlAddress3);
        Assert.assertEquals(emptyUrlResponseDto, testedUrl3);

    }

    @Test
    @DisplayName("Test get url by hash")
    void testGetUrlByHash() {

        String hash1 =  "h11111";
        UrlResponseDto testedUrl1 = urlService.getUrlByHash(hash1);
        Assertions.assertEquals(hash1, testedUrl1.hash());

        String hash2 =  "h22222";
        UrlResponseDto testedUrl2= urlService.getUrlByHash(hash2);
        Assertions.assertEquals(hash2, testedUrl2.hash());

        String hash3 =  "notExists";
        UrlResponseDto testedUrl3 = urlService.getUrlByHash(hash3);
        Assertions.assertNull(testedUrl3.hash());

    }
}