package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.app.AppPropertiesConfig;
import faang.school.urlshortenerservice.config.redis.RedisPropertiesConfig;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    private String url;
    private Url entity;
    private String hash;

    @BeforeEach
    public void setUp() {
        url = "https://faang-school.com/courses";
        hash = "777";
        entity = Url.builder()
                .url(url)
                .hash(hash)
                .build();
        RedisPropertiesConfig redisPropertiesConfig = new RedisPropertiesConfig(10);
        AppPropertiesConfig appPropertiesConfig = new AppPropertiesConfig("https://localhost:8077");
        urlService = new UrlService(hashCache, urlRepository, urlCacheRepository, redisPropertiesConfig, appPropertiesConfig);
    }

    @Test
    @DisplayName("Create short link from original link: success case")
    void testCreateShortLink_Success() {
        when(hashCache.takeCache()).thenReturn(hash);
        when(urlRepository.save(any(Url.class))).thenReturn(entity);

        String shortUrl = urlService.createShortLink(url);
        assertEquals("https://localhost:8077/777", shortUrl);
    }

    @Test
    @DisplayName("Get original link: success case")
    void testGetOriginalUrl_Success() {
        when(urlCacheRepository.getValue(hash)).thenReturn(url);

        String originalUrl = urlService.getOriginalUrl(hash);
        assertEquals(url, originalUrl);
    }

    @Test
    @DisplayName("Get original link: not found in hash but found in database")
    void testGetOriginalUrl_NotFoundInHashButFoundInDatabase() {
        when(urlCacheRepository.getValue(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(entity));

        assertEquals(url, urlService.getOriginalUrl(hash));
    }

    @Test
    @DisplayName("Get original link: not found in hash and not found in database")
    void testGetOriginalUrl_NotFoundInHashAndNotFoundInDatabase() {
        when(urlCacheRepository.getValue(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenThrow(new EntityNotFoundException(String.format("Url with hash %s not found", hash)));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrl(hash));
        assertEquals(String.format("Url with hash %s not found", hash), ex.getMessage());
    }
}
