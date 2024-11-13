package faang.school.urlshortenerservice.service.url;
import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.entity.Url;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

        when(hashCache.getHash()).thenReturn(TEST_HASH);
    }

    @Nested
    @DisplayName("Tests for creating a short URL")
    class CreateShortUrlTests {

        @Test
        @DisplayName("Should successfully create and return a short URL")
        void whenCreatingShortUrlThenShouldReturnSuccessfully() {
            when(urlMapper.toEntity(any(UrlDto.class))).thenReturn(urlEntity);
            when(urlRepository.save(any(Url.class))).thenReturn(urlEntity);

            UrlDto result = urlService.createShortUrl(urlRequestDto);

            assertEquals(TEST_URL, result.getUrl());
            assertEquals(TEST_HASH, result.getHash());
            verify(urlRepository).save(any(Url.class));
            verify(urlCacheRepository).save(TEST_HASH, TEST_URL);
        }

        @Test
        @DisplayName("Should save URL with correct hash and URL in repository and cache")
        void whenSavingUrlThenShouldSaveInRepositoryAndCache() {
            when(urlMapper.toEntity(any(UrlDto.class))).thenReturn(urlEntity);
            when(urlRepository.save(any(Url.class))).thenReturn(urlEntity);

            urlService.createShortUrl(urlRequestDto);

            verify(urlRepository).save(argThat(url -> url.getHash().equals(TEST_HASH) && url.getUrl().equals(TEST_URL)));
            verify(urlCacheRepository).save(TEST_HASH, TEST_URL);
        }
    }
}
