package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UrlServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private static final Logger LOG = LoggerFactory.getLogger(UrlServiceTest.class);
    @Autowired
    private UrlService service;
    @SpyBean
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private CacheManager cacheManager;

    private static final String TEST_HASH = "asdf";
    private static final String TEST_URL = "http://localhost:8080/test";

    @BeforeEach
    void setUp() {
        // Очистка кэша
        cacheManager.getCache("urls").clear();

        // Удаление только тестовых данных
        urlRepository.deleteById(TEST_HASH);

        // Создание тестовых данных
        urlRepository.save(Url.builder()
                .hash(TEST_HASH)
                .url(TEST_URL)
                .build());

        urlRepository.save(Url.builder()
                .hash(TEST_HASH + 2)
                .url(TEST_URL + "/2")
                .build());
    }

    @Test
    void testFindUrlById() {
        // первое обращение к сервису, получение данных из БД и кэширование
        UrlDto url = service.getUrl(TEST_HASH);
        assertNotNull(url, "Url is not found");
        LOG.info("Url: " + url.url());

        // второе обращение к сервису, получение данных из кэша
        UrlDto cachedUrl = service.getUrl(TEST_HASH);
        assertNotNull(cachedUrl, "Url is not found");
        LOG.info("Url: " + cachedUrl.url());

        // данные из БД извлекаются только 1 раз при первом обращении
        verify(urlRepository, times(1)).findById(TEST_HASH);
    }

    @Test
    void testFindUrlByIdNullResult() {
        final String hash = "ffff";

        assertThrows(EntityNotFoundException.class, () -> service.getUrl(hash));
    }

    @Test
    void testSaveUrlAndPutCache() {
        ShortUrlDto shortUrl = service.createShortUrl(new UrlDto("http://localhost:8080/newTest/new/test"));
        LOG.info(shortUrl.url());

        String hash = StringUtils.substringAfterLast(shortUrl.url(), "/");
        LOG.info("HASH: " + hash);

        UrlDto url = service.getUrl(hash);
        assertNotNull(url, "Url is not found");
        LOG.info("Url: " + url.url());

        verify(urlRepository, never()).findById(hash);
    }
}
