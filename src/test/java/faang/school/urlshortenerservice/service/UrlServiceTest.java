package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository cacheRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    private static final String EXAMPLE_URL = "https://example.com";
    private static final String EXAMPLE_SHORT_URL = "abc123";

    @Test
    void testGenerateShortUrlReturnsCachedHashWhenExists() {

        String original = EXAMPLE_URL;
        when(cacheRepository.getHashByOriginal(original)).thenReturn(EXAMPLE_SHORT_URL);
        String result = urlService.generateShortUrl(original);

        assertEquals(EXAMPLE_SHORT_URL, result);
        verify(hashCache, never()).getHash();
        verify(urlRepository, never()).save(any());
        verify(cacheRepository, never()).cache(any());
    }

    @Test
    void testGenerateShortUrlGeneratesHashSavesToDbAndCaches() {

        String original = EXAMPLE_URL;
        when(cacheRepository.getHashByOriginal(original)).thenReturn(null);
        when(hashCache.getHash()).thenReturn(EXAMPLE_SHORT_URL);
        String result = urlService.generateShortUrl(original);

        assertEquals(EXAMPLE_SHORT_URL, result);
        ArgumentCaptor<UrlEntity> captor = ArgumentCaptor.forClass(UrlEntity.class);
        verify(urlRepository).save(captor.capture());
        verify(cacheRepository).cache(captor.capture());
        UrlEntity saved = captor.getAllValues().get(0);

        assertEquals(EXAMPLE_SHORT_URL, saved.getHash());
        assertEquals(original, saved.getOriginalUrl());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testGetOriginalUrlReturnsCachedOriginalWhenExists() {

        String hash = EXAMPLE_SHORT_URL;
        when(cacheRepository.getOriginalByHash(hash)).thenReturn(EXAMPLE_URL);
        String result = urlService.getOriginalUrl(hash);

        assertEquals(EXAMPLE_URL, result);
        verify(urlRepository, never()).findByHash(any());
    }

    @Test
    void testGetOriginalUrlFetchesFromDbAndCachesIt() {

        String hash = EXAMPLE_SHORT_URL;
        when(cacheRepository.getOriginalByHash(hash)).thenReturn(null);
        UrlEntity entity = new UrlEntity(hash, EXAMPLE_URL, LocalDateTime.now());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(entity));
        String result = urlService.getOriginalUrl(hash);

        assertEquals(EXAMPLE_URL, result);
        verify(cacheRepository).cache(entity);
    }

    @Test
    void testGetOriginalUrlNotFound() {

        String hash = "unknown";
        when(cacheRepository.getOriginalByHash(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());
        DataAccessException ex = assertThrows(DataAccessException.class,
                () -> urlService.getOriginalUrl(hash));

        assertEquals("Specified short URL does not exist", ex.getMessage());
        verify(cacheRepository, never()).cache(any());
    }
}
