package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.ShortUrlResponse;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    private static final String BASE_URL = "https://short.com/";
    private static final String TEST_HASH = "abc123";
    private static final String TEST_URL = "https://example.com/very/long/url";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", BASE_URL);
    }

    @Test
    void testCreateShortUrlSuccess() {
        // Given
        when(hashCache.getHash()).thenReturn(TEST_HASH);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ShortUrlResponse response = urlService.createShortUrl(TEST_URL);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getShortUrl()).isEqualTo(BASE_URL + TEST_HASH);
        assertThat(response.getHash()).isEqualTo(TEST_HASH);

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlCacheRepository, times(1)).save(TEST_HASH, TEST_URL);
    }

    @Test
    void testCreateShortUrlSavesCorrectEntityToDatabase() {
        // Given
        when(hashCache.getHash()).thenReturn(TEST_HASH);
        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);

        // When
        urlService.createShortUrl(TEST_URL);

        // Then
        verify(urlRepository).save(urlCaptor.capture());
        Url savedUrl = urlCaptor.getValue();

        assertThat(savedUrl.getHash()).isEqualTo(TEST_HASH);
        assertThat(savedUrl.getUrl()).isEqualTo(TEST_URL);
        assertThat(savedUrl.getCreatedAt()).isNotNull();
    }

    @Test
    void testCreateShortUrlSavesToRedisCache() {
        // Given
        when(hashCache.getHash()).thenReturn(TEST_HASH);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        urlService.createShortUrl(TEST_URL);

        // Then
        verify(urlCacheRepository).save(TEST_HASH, TEST_URL);
    }

    @Test
    void testCreateShortUrlReturnsCorrectShortUrl() {
        // Given
        when(hashCache.getHash()).thenReturn(TEST_HASH);

        // When
        ShortUrlResponse response = urlService.createShortUrl(TEST_URL);

        // Then
        assertThat(response.getShortUrl()).startsWith(BASE_URL);
        assertThat(response.getShortUrl()).endsWith(TEST_HASH);
    }

    @Test
    void testGetOriginalUrlFromCache() {
        // Given
        when(urlCacheRepository.get(TEST_HASH)).thenReturn(TEST_URL);

        // When
        String result = urlService.getOriginalUrl(TEST_HASH);

        // Then
        assertThat(result).isEqualTo(TEST_URL);
        verify(urlCacheRepository).get(TEST_HASH);
        verify(urlRepository, never()).findById(anyString());
    }

    @Test
    void testGetOriginalUrlFromDatabaseWhenNotInCache() {
        // Given
        Url url = Url.builder()
                .hash(TEST_HASH)
                .url(TEST_URL)
                .build();

        when(urlCacheRepository.get(TEST_HASH)).thenReturn(null);
        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.of(url));

        // When
        String result = urlService.getOriginalUrl(TEST_HASH);

        // Then
        assertThat(result).isEqualTo(TEST_URL);
        verify(urlCacheRepository).get(TEST_HASH);
        verify(urlRepository).findById(TEST_HASH);
        verify(urlCacheRepository).save(TEST_HASH, TEST_URL);
    }

    @Test
    void testGetOriginalUrlThrowsExceptionWhenNotFound() {
        // Given
        when(urlCacheRepository.get(TEST_HASH)).thenReturn(null);
        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> urlService.getOriginalUrl(TEST_HASH))
                .isInstanceOf(UrlNotFoundException.class)
                .hasMessageContaining("URL not found for hash");

        verify(urlCacheRepository).get(TEST_HASH);
        verify(urlRepository).findById(TEST_HASH);
    }

    @Test
    void testGetOriginalUrlCachesResultFromDatabase() {
        // Given
        Url url = Url.builder()
                .hash(TEST_HASH)
                .url(TEST_URL)
                .build();

        when(urlCacheRepository.get(TEST_HASH)).thenReturn(null);
        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.of(url));

        // When
        urlService.getOriginalUrl(TEST_HASH);

        // Then - only called once when saving to cache after retrieving from DB
        verify(urlCacheRepository, times(1)).save(TEST_HASH, TEST_URL);
    }

    @Test
    void testCreateShortUrlWithDifferentHashes() {
        // Given
        String hash1 = "hash1";
        String hash2 = "hash2";
        when(hashCache.getHash()).thenReturn(hash1, hash2);

        // When
        ShortUrlResponse response1 = urlService.createShortUrl(TEST_URL);
        ShortUrlResponse response2 = urlService.createShortUrl(TEST_URL);

        // Then
        assertThat(response1.getHash()).isEqualTo(hash1);
        assertThat(response2.getHash()).isEqualTo(hash2);
        assertThat(response1.getShortUrl()).isNotEqualTo(response2.getShortUrl());
    }

    @Test
    void testCreateShortUrlCallsAllDependencies() {
        // Given
        when(hashCache.getHash()).thenReturn(TEST_HASH);

        // When
        urlService.createShortUrl(TEST_URL);

        // Then
        verify(hashCache).getHash();
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(anyString(), anyString());
    }
}