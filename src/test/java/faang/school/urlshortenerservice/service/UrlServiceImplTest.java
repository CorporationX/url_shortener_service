package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {
    private static final String TEST_DOMAIN = "https://short.com";
    private static final String TEST_HASH = "abc123";
    private static final String TEST_ORIGINAL_URL = "https://original.com/long-url";
    private static final String SHORT_URL = TEST_DOMAIN + "/" + TEST_HASH;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "domain", TEST_DOMAIN);
    }

    @Test
    void getOriginalUrl_shouldReturnFromCacheWhenExists() {
        when(urlCacheRepository.findByHash(TEST_HASH)).thenReturn(Optional.of(TEST_ORIGINAL_URL));

        String result = urlService.getOriginalUrl(TEST_HASH);

        assertEquals(TEST_ORIGINAL_URL, result);
        verify(urlCacheRepository).findByHash(TEST_HASH);
        verify(urlRepository, never()).findByHash(anyString());
    }

    @Test
    void getOriginalUrl_shouldReturnFromRepositoryAndUpdateCacheWhenCacheMiss() {
        when(urlCacheRepository.findByHash(TEST_HASH)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(TEST_HASH)).thenReturn(Optional.of(TEST_ORIGINAL_URL));

        String result = urlService.getOriginalUrl(TEST_HASH);

        assertEquals(TEST_ORIGINAL_URL, result);
        verify(urlCacheRepository).save(TEST_HASH, TEST_ORIGINAL_URL);
        verify(urlRepository).findByHash(TEST_HASH);
    }

    @Test
    void getOriginalUrl_shouldThrowExceptionWhenUrlNotFound() {
        when(urlCacheRepository.findByHash(TEST_HASH)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(TEST_HASH)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(TEST_HASH));
    }

    @Test
    void createShortUrl_shouldGenerateHashSaveMappingAndReturnFormattedUrl() {
        when(hashCache.getHash()).thenReturn(TEST_HASH);

        String result = urlService.createShortUrl(TEST_ORIGINAL_URL);

        assertEquals(SHORT_URL, result);
        verify(hashCache).getHash();
        verify(urlRepository).save(TEST_HASH, TEST_ORIGINAL_URL);
        verify(urlCacheRepository).save(TEST_HASH, TEST_ORIGINAL_URL);
    }

    @Test
    void createShortUrl_shouldUseDifferentHashesForDifferentCalls() {
        when(hashCache.getHash()).thenReturn("firstHash").thenReturn("secondHash");

        String firstResult = urlService.createShortUrl(TEST_ORIGINAL_URL);
        String secondResult = urlService.createShortUrl(TEST_ORIGINAL_URL);

        assertNotEquals(firstResult, secondResult);
        verify(hashCache, times(2)).getHash();
    }

    @Test
    void buildShortUrl_shouldCorrectlyFormatUrl() {
        String hash = "testHash";
        String result = ReflectionTestUtils.invokeMethod(urlService, "buildShortUrl", hash);

        assertEquals(TEST_DOMAIN + "/" + hash, result);
    }
}
