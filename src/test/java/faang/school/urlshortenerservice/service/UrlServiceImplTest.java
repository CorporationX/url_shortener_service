package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.properties.CacheProperties;
import faang.school.urlshortenerservice.config.properties.ClearProperties;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapperImpl;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.CacheService;
import faang.school.urlshortenerservice.service.cache.HashCacheService;
import faang.school.urlshortenerservice.service.outbox.OutboxCreateUrlType;
import faang.school.urlshortenerservice.service.outbox.OutboxService;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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
    private ClearProperties clearProperties;

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

        clearProperties.setBatchSize(2);
        clearProperties.setDaysThreshold(30);

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
    public void testGetUrl_counterBelowThreshold() {
        when(cacheService.getValue(hash, String.class)).thenReturn(Optional.of(url));

        String result = urlService.getUrl(hash);

        assertEquals(url, result);
        verify(cacheService).incrementAndGet(counterKey);
        verify(cacheService).getValue(hash, String.class);
    }

    @Test
    public void testGetUrl_counterAboveThreshold_andUrlInCache() {
        when(cacheService.getValue(hash, String.class)).thenReturn(Optional.of(url));

        String result = urlService.getUrl(hash);

        assertEquals(url, result);
        verify(cacheService).addExpire(hash, Duration.ofMillis(60000L));
        verify(cacheService).delete(counterKey);
    }

    @Test
    public void testGetUrl_counterAboveThreshold_andUrlNotInCache() {
        when(cacheService.getValue(hash, String.class)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(entityUrl));

        String result = urlService.getUrl(hash);

        assertEquals(url, result);
        verify(cacheService).put(hash, url, Duration.ofMillis(60000L));
        verify(cacheService).delete(counterKey);
    }

    @Test
    void generateHashForUrl_shouldSaveUrlAndReturnUrl() {
        when(urlRepository.existsByUrl(url)).thenReturn(false);
        when(hashCacheService.getHash()).thenReturn(hash);

        String shortenedUrl = urlService.generateHashForUrl(urlDto);

        verify(entityManager).persist(entityUrl);
        verify(outboxService).saveOutbox(entityUrl, OutboxCreateUrlType.OUTBOX_TYPE_ID);
        assertEquals(url, shortenedUrl);
    }

    @Test
    void generateHashForUrl_entityExists() {
        when(urlRepository.existsByUrl(url)).thenReturn(true);

        String shortenedUrl = urlService.generateHashForUrl(urlDto);

        verify(entityManager, never()).persist(any());
        verify(outboxService, never()).saveOutbox(any(Url.class), anyInt());
        assertEquals(url, shortenedUrl);
    }

    @Test
    void clearOutdatedUrls() {
        int batchSize = clearProperties.getBatchSize();
        List<String> first = List.of("hash1", "hash2"), second = List.of("hash3");
        when(urlRepository.deleteOutdatedUrls(any(LocalDateTime.class), eq(batchSize)))
                .thenReturn(first)
                .thenReturn(second)
                .thenReturn(Collections.emptyList());

        urlService.clearOutdatedUrls();

        verify(hashCacheService).addHash(first);
        verify(hashCacheService).addHash(second);
        verify(hashCacheService).addHash(Collections.emptyList());
    }
}
