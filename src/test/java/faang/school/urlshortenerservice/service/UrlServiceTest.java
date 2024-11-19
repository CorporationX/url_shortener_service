package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.TestContainersConfig;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.LocalHashCache;
import faang.school.urlshortenerservice.service.cache.UrlRedisCacheService;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class UrlServiceTest extends TestContainersConfig {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    UrlService urlService;

    @SpyBean
    UrlRepository urlRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataSource dataSource;

    @MockBean
    LocalHashCache localHashCache;

    @SpyBean
    UrlRedisCacheService urlRedisCacheService;

    @Captor
    ArgumentCaptor<Url> urlArgumentCaptor;
    
    private static final String testHash = "XZHO";
    private static final String testUrl = "https://vkontakte.ru";


    @BeforeEach
    void init() throws SQLException, LiquibaseException {
        redisTemplate.opsForValue().set(testHash, testUrl);

        try (Connection connection = dataSource.getConnection()) {
            Liquibase liquibase = new Liquibase(
                    "db/db.changelog-master.yaml",
                    new ClassLoaderResourceAccessor(),
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))
            );
            liquibase.dropAll();
            liquibase.update("");
        }
    }

    @AfterEach
    void clear() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void testGetOriginalUrl() {
        var result = urlService.getOriginalUrl(testHash);
        assertEquals(testUrl, result.getUrl());
    }

    @Test
    void testGetOriginalUrl_NotFoundInRedis() {
        redisTemplate.delete(testHash);
        var result = urlService.getOriginalUrl(testHash);

        verify(urlRepository).findByHash(testHash);

        assertEquals(testUrl, result.getUrl());
    }

    @Test
    void testGetAndDeleteUnusedUrls() {
        var result = urlService.getAndDeleteUnusedHashes();
        assertEquals(5, result.size());
        assertTrue(urlRepository.findAll().isEmpty());
    }

    @Test
    void testMakeShortUrl() {
        String receivedUrl = "https://google.ru";
        String hash = "XXXX";

        when(localHashCache.getHash()).thenReturn(hash);
        var shortUrl = urlService.makeShortUrl(receivedUrl);

        assertEquals("localhost:8080/XXXX", shortUrl);

        verify(urlRepository).save(urlArgumentCaptor.capture());
        assertEquals(hash, urlArgumentCaptor.getValue().getHash());
        assertEquals(receivedUrl, urlArgumentCaptor.getValue().getUrl());
        verify(urlRedisCacheService).save(hash, receivedUrl);
        assertEquals(receivedUrl, urlRedisCacheService.get(hash).get());
    }

    @Test
    void testGetUrl() {
        var result = urlService.getUrl(testHash);
        assertEquals(testUrl, result.getUrl());
    }
}