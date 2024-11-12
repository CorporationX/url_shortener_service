package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.CacheProperties;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapperImpl;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.CacheService;
import faang.school.urlshortenerservice.service.cache.HashCacheService;
import faang.school.urlshortenerservice.service.outbox.OutboxService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private OutboxService outboxService;

    @Mock
    private CacheService<String> cacheService;

    @Mock
    private HashCacheService hashCacheService;

    @Spy
    private CacheProperties cacheProperties;

    @Spy
    private UrlMapperImpl urlMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UrlServiceImpl urlService;

    private String hash;
    private String url;
    private Url entityUrl;
    private UrlDto urlDto;
    private String counterKey;

    @BeforeEach
    public void setUp() {
        hash = "testHash";
        url = "http://example.com";
        entityUrl = Url.builder()
                .hash(hash)
                .url(url)
                .build();
        urlDto = UrlDto.builder()
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

    @Test
    void shortenUrl_shouldSaveUrlAndReturnUrl() {
        when(urlRepository.existsByUrl(url)).thenReturn(false);
        when(hashCacheService.getHash()).thenReturn(hash);

        String shortenedUrl = urlService.shortenUrl(urlDto);

        verify(entityManager).persist(entityUrl);
        verify(outboxService).saveOutbox(entityUrl);
        assertEquals(url, shortenedUrl);
    }

    @Test
    void shortenUrl_entityExists() {
        String correctMessage = "Url %s already exists".formatted(url);
        when(urlRepository.existsByUrl(url)).thenReturn(true);

        Exception exception = assertThrows(EntityExistsException.class,
                () -> urlService.shortenUrl(urlDto));

        verify(entityManager, never()).persist(any());
        verify(outboxService, never()).saveOutbox(any(Url.class));
        assertEquals(correctMessage, exception.getMessage());
    }
}
