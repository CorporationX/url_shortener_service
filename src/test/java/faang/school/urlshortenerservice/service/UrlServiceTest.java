package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.service.url.UrlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashService hashService;

    @Mock
    private Executor taskExecutor;

    @InjectMocks
    private UrlServiceImpl urlService;

    @BeforeEach
    void setUp() {
        BlockingQueue<String> query = new ArrayBlockingQueue<>(10, true, List.of("hash0"));

        ReflectionTestUtils.setField(urlService, "hashSize", 10);
        ReflectionTestUtils.setField(urlService, "cacheSize", 10);
        ReflectionTestUtils.setField(urlService, "minPercentageHashes", 20);
        ReflectionTestUtils.setField(urlService, "hashCache", query);

    }

    @Test
    void testShortenUrl() {
        String originalUrl = "http://example.com";
        String expectedHash = "hash0";
        when(urlRepository.save(any())).thenReturn(null);
        when(urlCacheRepository.save(any())).thenReturn(null);

        Mono<String> result = urlService.shortenUrl(originalUrl);
        String actualHash = result.block();

        assertEquals(expectedHash, actualHash);

        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);
        verify(urlRepository).save(urlCaptor.capture());
        assertEquals(originalUrl, urlCaptor.getValue().getUrl());
        assertEquals(expectedHash, urlCaptor.getValue().getHash());
    }

    @Test
    void testGetOriginalUrlWhenInCache() {
        String hash = "hash0";
        Url url = new Url(hash, "http://example.com", LocalDateTime.now());
        when(urlCacheRepository.get(hash)).thenReturn(Mono.just(url.getUrl()));
        when(urlRepository.findById(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash).block();

        assertEquals("http://example.com", result);
    }

    @Test
    void testGetOriginalUrlWhenNotInCache() {
        String hash = "hash1";
        Url url = new Url(hash, "http://example.com", LocalDateTime.now());
        when(urlCacheRepository.get(hash)).thenReturn(Mono.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash).block();

        assertEquals("http://example.com", result);
    }

    @Test
    void testGetOriginalUrlWhenNotFound() {
        String hash = "hash2";
        when(urlCacheRepository.get(hash)).thenReturn(Mono.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> urlService.getOriginalUrl(hash).block());
    }
}
