package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;
    @Spy
    private UrlMapper urlMapper;
    @Mock
    private UrlJpaRepository urlRepository;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlCacheRepository urlCacheRepository;

    private Url url;
    private UrlDto urlDto;
    private String hash;
    private String cachedUrl;

    @BeforeEach
    void setUp() {
        String urlString = "url";
        cachedUrl = "cachedUrl";
        hash = "hash";
        url = Url.builder()
                .url(urlString)
                .build();
        urlDto = UrlDto.builder()
                .url(urlString)
                .build();
    }

    @Test
    @DisplayName("testing createShortUrl method")
    void testCreateShortUrl() {
        when(urlMapper.toEntity(urlDto)).thenReturn(url);
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(url)).thenReturn(url);

        urlService.createShortUrl(urlDto);

        verify(urlMapper, times(1)).toEntity(urlDto);
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(url);
        verify(urlCacheRepository, times(1)).save(eq(hash), eq(url.getUrl()), any());
        verify(urlMapper, times(1)).toDto(url);
    }

    @Nested
    @DisplayName("Method: getUrlByHash")
    class getUrlByHash {
        @Test
        @DisplayName("testing getUrlByHash with finding in cache")
        void testGetUrlByHashFoundInCache() {
            when(urlCacheRepository.get(hash)).thenReturn(Optional.of(cachedUrl));

            String urlByHash = urlService.getUrlByHash(hash);

            verify(urlCacheRepository, times(1)).get(hash);
            assertEquals(cachedUrl, urlByHash);
        }

        @Test
        @DisplayName("testing getUrlByHash with finding in repository")
        void testGetUrlByHashFoundInRepository() {
            when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
            when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

            String urlByHash = urlService.getUrlByHash(hash);

            verify(urlCacheRepository, times(1)).get(hash);
            verify(urlRepository, times(1)).findByHash(hash);
            verify(urlCacheRepository, times(1)).save(eq(hash), eq(url.getUrl()), any());
            assertEquals(url.getUrl(), urlByHash);
        }

        @Test
        @DisplayName("testing getUrlByHash with not finding such hash")
        void testGetUrlByHashWithNotFinding() {
            when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
            when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> urlService.getUrlByHash(hash));

            verify(urlCacheRepository, times(1)).get(hash);
            verify(urlRepository, times(1)).findByHash(hash);
        }
    }
}