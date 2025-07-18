package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.LocalCache;
import faang.school.urlshortenerservice.service.cache.UrlRetrieverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {
    private static final long CACHE_TTL_HOURS = 1;
    private static final String TEST_HASH = "Uf";
    private static final String LONG_URL = "https://example.com/a-very-long-url";

    @Mock
    private LocalCache localCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRetrieverService urlRetrieverService;

    @InjectMocks
    private UrlServiceImpl urlService;

    @Captor
    private ArgumentCaptor<Url> urlArgumentCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "cacheTtlHours", CACHE_TTL_HOURS);
    }

    @Nested
    @DisplayName("getShortUrl tests")
    class GetShortUrlTests {

        @Test
        @DisplayName("Should return hash and save URL when all operation succeed")
        void getShortUrl_Success() {
            when(localCache.getHash()).thenReturn(TEST_HASH);
            String resultHash = urlService.getShortUrl(LONG_URL);

            assertEquals(TEST_HASH, resultHash);

            verify(urlRepository).save(urlArgumentCaptor.capture());
            Url savedUrl = urlArgumentCaptor.getValue();
            assertEquals(TEST_HASH, savedUrl.getHash());
            assertEquals(LONG_URL, savedUrl.getUrl());
            verify(urlCacheRepository).save(TEST_HASH, LONG_URL, Duration.ofHours(CACHE_TTL_HOURS));
        }

        @Test
        @DisplayName("Should throw RuntimeException when localCache fails")
        void getShortUrl_LocalCacheFails() {
            when(localCache.getHash()).thenThrow(new RuntimeException("Cache error"));

            RuntimeException exception =
                    assertThrows(RuntimeException.class, () -> urlService.getShortUrl(LONG_URL));

            assertEquals("Failed to generate short URL. Please try again later.", exception.getMessage());
            verifyNoInteractions(urlRepository, urlCacheRepository);
        }

        @Test
        @DisplayName("Should throw RuntimeException when urlRepository fails")
        void getShortUrl_UrlRepositoryFails() {
            when(localCache.getHash()).thenReturn(TEST_HASH);
            doThrow(new RuntimeException("DB error")).when(urlRepository).save(any(Url.class));

            RuntimeException exception =
                    assertThrows(RuntimeException.class, () -> urlService.getShortUrl(LONG_URL));

            assertEquals("Failed to generate short URL. Please try again later.", exception.getMessage());
            verify(urlRepository).save(any(Url.class));
            verifyNoInteractions(urlCacheRepository);
        }
    }

    @Nested
    @DisplayName("getLongUrl tests")
    class GetLongUrlTests {

        @Test
        @DisplayName("Should return long URL when hash is found")
        void getLongUrl_Success() {
            when(urlRetrieverService.getLongUrl(TEST_HASH)).thenReturn(Optional.of(LONG_URL));

            String resultUrl = urlService.getLongUrl(TEST_HASH);

            assertEquals(LONG_URL, resultUrl);
            verify(urlRetrieverService).getLongUrl(TEST_HASH);
        }

        @Test
        @DisplayName("Should throw UrlNotFoundException when hash is not found")
        void getLongUrl_NotFound() {
            when(urlRetrieverService.getLongUrl(TEST_HASH)).thenReturn(Optional.empty());

            UrlNotFoundException exception =
                    assertThrows(UrlNotFoundException.class, () -> urlService.getLongUrl(TEST_HASH));

            assertTrue(exception.getMessage().contains(TEST_HASH));
            verify(urlRetrieverService).getLongUrl(TEST_HASH);
        }
    }
}
