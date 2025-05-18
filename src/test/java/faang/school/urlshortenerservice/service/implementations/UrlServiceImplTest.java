package faang.school.urlshortenerservice.service.implementations;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlServiceImpl urlService;

    private final String baseShortenerUrl = "http://short.url/";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseShortenerUrl", baseShortenerUrl);
    }

    @Test
    void testCreateShortUrl_ShouldReturnShortUrl() {
        String hash = "abc123";
        String originalUrl = "http://example.com";
        UrlRequest request = new UrlRequest(originalUrl);
        Url url = Url.builder().hash(hash).url(originalUrl).build();

        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        String result = urlService.createShortUrl(request);

        assertEquals(baseShortenerUrl + hash, result);
        verify(hashCache).getHash();
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(hash, originalUrl);
    }

    @Test
    void testGetOriginalUrl_ShouldReturnFromCache_WhenUrlInCache() {
        String hash = "abc123";
        String cachedUrl = "http://example.com";

        when(urlCacheRepository.findByHash(hash)).thenReturn(cachedUrl);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(cachedUrl, result);
        verify(urlCacheRepository).findByHash(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void testGetOriginalUrl_ShouldReturnFromRepository_WhenUrlNotInCache() {
        String hash = "abc123";
        String originalUrl = "http://example.com";
        Url url = Url.builder().hash(hash).url(originalUrl).build();

        when(urlCacheRepository.findByHash(hash)).thenReturn("");
        when(urlRepository.findById(hash)).thenReturn(java.util.Optional.of(url));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findById(hash);
    }

    @Test
    void testGetOriginalUrl_ShouldThrowException_WhenUrlNotFound() {
        String hash = "nonexistent";

        when(urlCacheRepository.findByHash(hash)).thenReturn("");
        when(urlRepository.findById(hash)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> urlService.getOriginalUrl(hash));
        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findById(hash);
    }

    @Test
    void testGetOriginalUrl_ShouldReturnFromRepository_WhenCacheReturnsNull() {
        String hash = "abc123";
        String originalUrl = "http://example.com";
        Url url = Url.builder().hash(hash).url(originalUrl).build();

        when(urlCacheRepository.findByHash(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(java.util.Optional.of(url));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findById(hash);
    }
}