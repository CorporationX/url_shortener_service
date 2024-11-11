package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.CacheProperties;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.CacheService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private CacheService<String> cacheService;

    @Spy
    private CacheProperties cacheProperties;

    @InjectMocks
    private UrlServiceImpl urlService;

    private String hash;
    private String url;
    private Url entityUrl;
    private String counterKey;

    @BeforeEach
    public void setUp() {
        hash = "testHash";
        url = "http://example.com";
        entityUrl = Url.builder()
                .hash(hash)
                .url(url)
                .build();
        counterKey = hash + "::counter";

        cacheProperties.setRequestThreshold(10L);
        cacheProperties.setTtlIncrementTimeMs(60_000L);

        lenient().when(cacheService.incrementAndGet(counterKey)).thenReturn(15L);
    }

    @Test
    public void testGetUrlBy_cacheHit() {
        when(cacheService.getValue(hash, String.class)).thenReturn(Optional.of(url));

        String result = urlService.getUrlBy(hash);

        assertEquals(url, result);
        verify(cacheService).getValue(hash, String.class);
        verify(urlRepository, never()).findById(hash);
    }

    @Test
    public void testGetUrlBy_cacheMiss() {
        when(cacheService.getValue(hash, String.class)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(entityUrl));

        String result = urlService.getUrlBy(hash);

        assertEquals(url, result);
        verify(cacheService).getValue(hash, String.class);
        verify(urlRepository).findById(hash);
    }

    @Test
    public void testGetUrlFromDatabaseBy_found() {
        when(urlRepository.findById(hash)).thenReturn(Optional.of(entityUrl));

        String result = urlService.getUrlFromDatabaseBy(hash);

        assertEquals(url, result);
        verify(urlRepository).findById(hash);
    }

    @Test
    public void testGetUrlFromDatabaseBy_notFound() {
        String correctMessage = "Url with hash testHash not found";
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> urlService.getUrlFromDatabaseBy(hash));

        assertEquals(correctMessage, exception.getMessage());
        verify(urlRepository).findById(hash);
    }

    @Test
    public void testRedirectByHash_counterBelowThreshold() {
        when(cacheService.getValue(hash, String.class)).thenReturn(Optional.of(url));

        String result = urlService.redirectByHash(hash);

        assertEquals(url, result);
        verify(cacheService).incrementAndGet(counterKey);
        verify(cacheService).getValue(hash, String.class);
    }

    @Test
    public void testRedirectByHash_counterAboveThreshold_andUrlInCache() {
        when(cacheService.getValue(hash, String.class)).thenReturn(Optional.of(url));

        String result = urlService.redirectByHash(hash);

        assertEquals(url, result);
        verify(cacheService).addExpire(hash, Duration.ofMillis(60000L));
        verify(cacheService).delete(counterKey);
    }

    @Test
    public void testRedirectByHash_counterAboveThreshold_andUrlNotInCache() {
        when(cacheService.getValue(hash, String.class)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(entityUrl));

        String result = urlService.redirectByHash(hash);

        assertEquals(url, result);
        verify(cacheService).put(hash, url, Duration.ofMillis(60000L));
        verify(cacheService).delete(counterKey);
    }
}
