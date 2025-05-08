package faang.school.urlshortenerservice.service.implementations;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    private UrlDto urlDto;
    private String hash;
    private String originalUrl;
    private String shortUrlPath;

    @BeforeEach
    void setUp() {
        urlDto = new UrlDto("https://example.com");
        hash = "abc123";
        originalUrl = "https://example.com";
        shortUrlPath = "http://short.url/";
        urlService = new UrlServiceImpl(urlRepository, urlCacheRepository, hashCache);
        setField(urlService, "urlPath", shortUrlPath);
    }

    @Test
    void testGetShortUrl_Success() {
        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.getShortUrl(urlDto);

        assertEquals(shortUrlPath + hash, result);
        verify(hashCache).getHash();
        verify(urlRepository).saveUrlWithNewHash(hash, originalUrl);
        verify(urlCacheRepository).save(hash, originalUrl);
    }

    @Test
    void testGetOriginalUrl_FromCache_Success() {
        when(urlCacheRepository.get(hash)).thenReturn(originalUrl);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlCacheRepository).get(hash);
        verify(urlRepository, never()).getUrlByHash(hash);
    }

    @Test
    void testGetOriginalUrl_FromDatabase_Success() {
        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.getUrlByHash(hash)).thenReturn(originalUrl);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlCacheRepository).get(hash);
        verify(urlRepository).getUrlByHash(hash);
    }

    @Test
    void testGetOriginalUrl_NotFound_ThrowsException() {
        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.getUrlByHash(hash)).thenThrow(new EmptyResultDataAccessException(1));

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> urlService.getOriginalUrl(hash));
        assertEquals("Url with hash %s was not found in database".formatted(hash), exception.getMessage());
        verify(urlCacheRepository).get(hash);
        verify(urlRepository).getUrlByHash(hash);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}
