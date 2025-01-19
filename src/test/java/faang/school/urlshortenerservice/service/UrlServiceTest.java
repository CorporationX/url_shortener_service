package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.local_cache.HashCache;
import faang.school.urlshortenerservice.repository.RedisUrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private RedisUrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    private final String hash = "abc123";
    private final String expectedUrl = "https://example.com";

    @Test
    void testGetUrlReturnsUrlWhenExistsInCache() {
        when(urlCacheRepository.get(hash)).thenReturn(expectedUrl);

        String actualUrl = urlService.getUrl(hash);

        assertEquals(expectedUrl, actualUrl);
        verify(urlCacheRepository, times(1)).get(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void testGetUrlReturnsUrlWhenExistsInRepository() {
        Url urlEntity = Url.builder()
                .hash(hash)
                .url(expectedUrl)
                .createdAt(LocalDateTime.now())
                .build();

        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(urlEntity));

        String actualUrl = urlService.getUrl(hash);

        assertEquals(expectedUrl, actualUrl);
        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, times(1)).findByHash(hash);
        verify(urlCacheRepository, times(1)).save(hash, expectedUrl);
    }

    @Test
    void testGetUrlThrowsExceptionWhenHashNotFound() {
        String hash = "nonexistent123";

        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getUrl(hash));

        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, times(1)).findByHash(hash);
        verifyNoMoreInteractions(urlCacheRepository, urlRepository);
    }
}