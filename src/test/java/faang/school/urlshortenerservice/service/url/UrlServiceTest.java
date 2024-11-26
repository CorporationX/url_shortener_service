package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.config.properties.UrlLifeTimeConfig;
import faang.school.urlshortenerservice.exception.UrlExpiredException;
import faang.school.urlshortenerservice.model.url.Url;
import faang.school.urlshortenerservice.repository.postgres.url.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import faang.school.urlshortenerservice.util.HashCache;
import faang.school.urlshortenerservice.validator.AppUrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    private static final String PATH_WITH_HASHED_URL = "http://localhost:8080/url/";
    private static final String LONG_URL = "https://faang-school.com/courses";
    private static final String HASH = "123abc";

    @Mock
    private AppUrlValidator appUrlValidator;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    private UrlLifeTimeConfig lifeTime;

    @InjectMocks
    private UrlService urlService;

    private Url url;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(urlService, "pathWithHashedUrl", PATH_WITH_HASHED_URL);
        lifeTime = UrlLifeTimeConfig.builder().months(12).days(0).hours(0).build();
        ReflectionTestUtils.setField(urlService, "lifeTime", lifeTime);

        url = Url.builder().build();
    }

    @Test
    void testGenerateShortUrl() {
        doNothing().when(appUrlValidator).validate(eq(LONG_URL));
        when(hashCache.getHash()).thenReturn(HASH);
        when(urlRepository.save(any(Url.class))).thenReturn(url);
        doNothing().when(urlCacheRepository).save(eq(HASH), eq(LONG_URL));

        String result = urlService.generateShortUrl(LONG_URL);

        assertEquals(PATH_WITH_HASHED_URL + HASH, result);
    }

    @Test
    void testGetUrlByHash_FromRedis() {
        when(urlCacheRepository.get(HASH)).thenReturn(LONG_URL);

        String result = urlService.getUrlByHash(HASH);

        assertEquals(LONG_URL, result);

        verify(urlRepository, never()).findByHash(anyString());
    }

    @Test
    void testGetUrlByHash_FromPostgres() {
        url.setUrl(LONG_URL);
        url.setHash(HASH);

        when(urlCacheRepository.get(HASH)).thenReturn(null);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.of(url));

        String result = urlService.getUrlByHash(HASH);

        assertEquals(LONG_URL, result);
    }

    @Test
    void testGetUrlByHash_UrlExpired() {
        when(urlCacheRepository.get(HASH)).thenReturn(null);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.empty());

        assertThrows(UrlExpiredException.class, () -> {
            urlService.getUrlByHash(HASH);
        });
    }

    @Test
    void testCleanHashes() {
        List<String> cleanedHashes = List.of("1", "a");
        when(urlRepository.getOldUrlsAndDelete()).thenReturn(cleanedHashes);
        List<String> result = urlService.cleanHashes();
        assertEquals(cleanedHashes, result);
    }
}