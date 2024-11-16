package faang.school.urlshortenerservice.service.url;
import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.dto.url.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.mapper.url.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    private static final String TEST_URL = "https://example.com";
    private static final String TEST_HASH = "123456";

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlMapper urlMapper;

    @InjectMocks
    private UrlService urlService;

    private UrlRequestDto urlRequestDto;
    private UrlDto urlDto;
    private Url urlEntity;

    @BeforeEach
    void setUp() {
        urlRequestDto = UrlRequestDto.builder()
                .url(TEST_URL)
                .build();

        urlDto = UrlDto.builder()
                .hash(TEST_HASH)
                .url(TEST_URL)
                .build();

        urlEntity = Url.builder()
                .hash(TEST_HASH)
                .url(TEST_URL)
                .build();
    }

    @Nested
    @DisplayName("Tests for creating a short URL")
    class CreateShortUrlTests {

        @Test
        @DisplayName("Should successfully create and return a short URL")
        void whenCreatingShortUrlThenShouldReturnSuccessfully() {
            when(hashCache.getHash()).thenReturn(TEST_HASH);
            when(urlMapper.toEntity(any(UrlDto.class))).thenReturn(urlEntity);
            when(urlRepository.save(any(Url.class))).thenReturn(urlEntity);

            UrlResponseDto result = urlService.createShortUrl(urlRequestDto);

            assert(result.getUrl().contains(TEST_HASH));
            verify(urlRepository).save(any(Url.class));
            verify(urlCacheRepository).save(TEST_HASH, TEST_URL);
        }

        @Test
        @DisplayName("Should save URL with correct hash and URL in repository and cache")
        void whenSavingUrlThenShouldSaveInRepositoryAndCache() {
            when(hashCache.getHash()).thenReturn(TEST_HASH);
            when(urlMapper.toEntity(any(UrlDto.class))).thenReturn(urlEntity);
            when(urlRepository.save(any(Url.class))).thenReturn(urlEntity);

            urlService.createShortUrl(urlRequestDto);

            verify(urlRepository).save(argThat(url -> url.getHash().equals(TEST_HASH) && url.getUrl().equals(TEST_URL)));
            verify(urlCacheRepository).save(TEST_HASH, TEST_URL);
        }
    }

    @Nested
    @DisplayName("Tests for retrieving a URL")
    class GetUrlTests {

        @Test
        @DisplayName("Should return URL from cache if available")
        void whenUrlIsCachedThenShouldReturnCachedUrl() {
            when(urlCacheRepository.getUrl(TEST_HASH)).thenReturn(TEST_URL);

            UrlDto result = urlService.getUrl(TEST_HASH);

            assertEquals(TEST_URL, result.getUrl());
            assertEquals(TEST_HASH, result.getHash());
            verify(urlCacheRepository).getUrl(TEST_HASH);
            verifyNoInteractions(urlRepository);
        }

        @Test
        @DisplayName("Should return URL from repository if not in cache")
        void whenUrlIsNotCachedThenShouldReturnFromRepository() {
            when(urlCacheRepository.getUrl(TEST_HASH)).thenReturn(null);
            when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.of(urlEntity));

            UrlDto result = urlService.getUrl(TEST_HASH);

            assertEquals(TEST_URL, result.getUrl());
            assertEquals(TEST_HASH, result.getHash());
            verify(urlCacheRepository).getUrl(TEST_HASH);
            verify(urlRepository).findById(TEST_HASH);
        }

        @Test
        @DisplayName("Should throw exception if URL not found in both cache and repository")
        void whenUrlIsNotFoundThenShouldThrowException() {
            when(urlCacheRepository.getUrl(TEST_HASH)).thenReturn(null);
            when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class, () -> urlService.getUrl(TEST_HASH));

            assertEquals("URL not found for hash: " + TEST_HASH, exception.getMessage());
            verify(urlCacheRepository).getUrl(TEST_HASH);
            verify(urlRepository).findById(TEST_HASH);
        }
    }
}

