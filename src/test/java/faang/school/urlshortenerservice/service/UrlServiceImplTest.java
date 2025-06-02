package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EmptyHashCacheException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases of UrlServiceImplTest")
public class UrlServiceImplTest {

    private static final Duration CACHE_TTL = Duration.ofDays(1);
    private static final String DEFAULT_URL = "http://example.com/";
    private static final String HASH = "abc123";
    private static final String ORIGINAL_URL = "https://test-service.com/very-long-path-with-more-params";
    private static final String EMPTY_HASH_CACHE = "Hash cache is empty";
    private static final String URL_NOT_FOUND = "URL not found by hash: " + HASH;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Captor
    private ArgumentCaptor<Url> urlCaptor;

    @InjectMocks
    private UrlServiceImpl urlService;

    private UrlRequestDto urlRequestDto;

    private Url url;

    @BeforeEach
    public void setUp() {
        urlRequestDto = new UrlRequestDto();
        urlRequestDto.setOriginalUrl(ORIGINAL_URL);

        url = new Url();
        url.setUrl(ORIGINAL_URL);
        url.setHash(HASH);

        ReflectionTestUtils.setField(urlService, "cacheTtl", CACHE_TTL);
        ReflectionTestUtils.setField(urlService, "defaultUrl", DEFAULT_URL);
    }

    @Test
    @DisplayName("createShortUrl - short URL already exist")
    public void testCreateShortUrlWhenShortUrlExist() {
        when(urlRepository.findByUrl(ORIGINAL_URL)).thenReturn(Optional.of(url));

        UrlResponseDto actualUrl = urlService.createShortUrl(urlRequestDto);

        verify(urlRepository, times(1)).findByUrl(ORIGINAL_URL);
        verify(hashCache, never()).getHash();
        verify(urlRepository, never()).save(any());
        verify(urlCacheRepository, never()).saveUrl(anyString(), anyString(), any());
        assertEquals(DEFAULT_URL + HASH, actualUrl.getShortUrl());
    }

    @Test
    @DisplayName("createShortUrl - empty hash cache")
    public void testCreateShortUrlWithEmptyHashCache() {
        when(urlRepository.findByUrl(ORIGINAL_URL)).thenReturn(Optional.empty());
        when(hashCache.getHash()).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EmptyHashCacheException.class,
                () -> urlService.createShortUrl(urlRequestDto)
        );

        assertEquals(EMPTY_HASH_CACHE, exception.getMessage());
        verify(urlRepository, never()).save(any());
        verify(urlCacheRepository, never()).saveUrl(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("createShortUrl - race condition to save URL")
    public void testCreateShortUrlWithRaceCondition() {
        when(urlRepository.findByUrl(ORIGINAL_URL))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Url()));
        when(hashCache.getHash()).thenReturn(Optional.of(HASH));
        when(urlRepository.save(any(Url.class))).thenThrow(new DataIntegrityViolationException("Insert error"));

        UrlResponseDto actualUrl = urlService.createShortUrl(urlRequestDto);

        verify(urlRepository, times(2)).findByUrl(ORIGINAL_URL);
        assertNotNull(actualUrl);
    }

    @Test
    @DisplayName("createShortUrl - success")
    public void testCreateShortUrlSuccess() {
        when(urlRepository.findByUrl(ORIGINAL_URL)).thenReturn(Optional.empty());
        when(hashCache.getHash()).thenReturn(Optional.of(HASH));

        UrlResponseDto actualUrl = urlService.createShortUrl(urlRequestDto);

        verify(urlRepository, times(1)).save(urlCaptor.capture());
        Url capturedUrl = urlCaptor.getValue();

        verify(urlRepository, times(1)).findByUrl(ORIGINAL_URL);
        verify(hashCache, times(1)).getHash();
        verify(urlCacheRepository, times(1)).saveUrl(HASH, ORIGINAL_URL, CACHE_TTL);

        assertEquals(HASH, capturedUrl.getHash());
        assertEquals(ORIGINAL_URL, capturedUrl.getUrl());
        assertEquals(DEFAULT_URL + HASH, actualUrl.getShortUrl());
    }

    @Test
    @DisplayName("getOriginalUrl - URL not found")
    public void testGetOriginalUrlNotFound() {
        when(urlCacheRepository.findUrlAndExpire(HASH, CACHE_TTL)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(HASH));

        assertEquals(URL_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("getOriginalUrl - not found in cache and get from DB")
    public void testGetOriginalUrlNotFoundInCache() {
        when(urlCacheRepository.findUrlAndExpire(HASH, CACHE_TTL)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.of(url));

        String actualUrl = urlService.getOriginalUrl(HASH);

        verify(urlCacheRepository, times(1)).findUrlAndExpire(HASH, CACHE_TTL);
        verify(urlCacheRepository, times(1)).saveUrl(HASH, ORIGINAL_URL, CACHE_TTL);
        assertEquals(ORIGINAL_URL, actualUrl);
    }

    @Test
    @DisplayName("getOriginalUrl - success")
    public void testGetOriginalUrlSuccess() {
        when(urlCacheRepository.findUrlAndExpire(HASH, CACHE_TTL)).thenReturn(Optional.of(ORIGINAL_URL));

        String actualUrl = urlService.getOriginalUrl(HASH);

        verify(urlCacheRepository, times(1)).findUrlAndExpire(HASH,CACHE_TTL);
        verify(urlRepository, never()).findByHash(anyString());
        verify(urlCacheRepository, never()).saveUrl(anyString(), anyString(), any());
        assertEquals(ORIGINAL_URL, actualUrl);
    }
}
