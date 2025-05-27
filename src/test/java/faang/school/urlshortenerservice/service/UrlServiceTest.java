package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.exception.UrlShorteningException;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlServiceImpl urlService;

    private static final String TEST_URL = "https://example.com";
    private static final String TEST_HASH = "abc123";
    private static final String BASE_URL = "https://short.url/";
    private static final long TTL = 172800;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrlHttps", BASE_URL);
        ReflectionTestUtils.setField(urlService, "ttlInSeconds", TTL);
    }

    @Test
    void testShortenSuccess() {
        RequestUrlDto request = new RequestUrlDto(TEST_URL);
        when(hashCache.getHash()).thenReturn(TEST_HASH);

        ResponseUrlDto response = urlService.shorten(request);

        assertNotNull(response);
        assertEquals(TEST_URL, response.getOriginalUrl());
        assertEquals(BASE_URL + TEST_HASH, response.getShortUrl());

        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(TEST_HASH, TEST_URL, TTL);
    }

    @Test
    void testShortenWhenHashCacheFailureThrowsException() {
        RequestUrlDto request = new RequestUrlDto(TEST_URL);
        when(hashCache.getHash()).thenThrow(new RuntimeException("Hash generation failed"));

        assertThrows(UrlShorteningException.class, () -> urlService.shorten(request));
        verify(urlRepository, never()).save(any());
        verify(urlCacheRepository, never()).save(anyString(), anyString(), anyLong());
    }

    @Test
    void testGetOriginalUrlWhenInCache_Success() {
        when(urlCacheRepository.get(TEST_HASH)).thenReturn(TEST_URL);

        ResponseUrlDto response = urlService.getOriginalUrl(TEST_HASH);

        assertNotNull(response);
        assertEquals(TEST_URL, response.getOriginalUrl());
        verify(urlRepository, never()).findById(anyString());
    }

    @Test
    void testGetOriginalUrlWhenNotInCacheSuccess() {
        when(urlCacheRepository.get(TEST_HASH)).thenReturn(null);
        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.of(new Url(TEST_HASH, TEST_URL, null)));

        ResponseUrlDto response = urlService.getOriginalUrl(TEST_HASH);

        assertNotNull(response);
        assertEquals(TEST_URL, response.getOriginalUrl());
        verify(urlCacheRepository).save(TEST_HASH, TEST_URL, TTL);
    }

    @Test
    void testGetOriginalUrlWhenNotFoundThrowsException() {
        when(urlCacheRepository.get(TEST_HASH)).thenReturn(null);
        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(TEST_HASH));
    }

    @Test
    void testGetOriginalUrlWhenCacheFailureFallbackToDatabase() {
        when(urlCacheRepository.get(TEST_HASH)).thenThrow(new RuntimeException("Cache failure"));
        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.of(new Url(TEST_HASH, TEST_URL, null)));

        ResponseUrlDto response = urlService.getOriginalUrl(TEST_HASH);

        assertNotNull(response);
        assertEquals(TEST_URL, response.getOriginalUrl());
    }
} 