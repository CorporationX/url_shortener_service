package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    void testGetUrlFromCache() {
        when(urlCacheRepository.findByHash("abc123")).thenReturn(Optional.of("https://example.com"));

        String originalUrl = urlService.getOriginalUrl("abc123");

        assertThat(originalUrl).isEqualTo("https://example.com");
        verify(urlCacheRepository, times(1)).findByHash("abc123");
        verify(urlRepository, never()).findByHash(anyString());
    }

    @Test
    void testCreateShortUrl_NewUrl() {
        String url = "https://example.com";
        String hash = "abc123";
        long userId = 1L;

        when(urlRepository.findByUrl(url)).thenReturn(Optional.empty());
        when(hashRepository.findAll()).thenReturn(List.of(new Hash(hash)));
        when(urlRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String shortUrl = urlService.createShortUrl(url, userId);

        assertThat(shortUrl).isEqualTo(hash);
        verify(urlRepository, times(1)).save(any());
        verify(urlCacheRepository, times(1)).save(hash, url);
    }

    @Test
    void testCreateShortUrl_DuplicateUrl() {
        String url = "https://example.com";
        String hash = "abc123";
        long userId = 1L;

        when(urlRepository.findByUrl(url)).thenReturn(Optional.of(new Url(hash, url, userId)));

        String result = urlService.createShortUrl(url, userId);

        assertThat(result).isEqualTo(hash);
        verify(urlRepository, times(1)).findByUrl(url);
        verify(hashRepository, never()).findAll();
        verify(urlRepository, never()).save(any());
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        when(urlCacheRepository.findByHash("abc123")).thenReturn(Optional.empty());
        when(urlRepository.findByHash("abc123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.getOriginalUrl("abc123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("URL not found for hash: abc123");

        verify(urlCacheRepository, times(1)).findByHash("abc123");
        verify(urlRepository, times(1)).findByHash("abc123");
    }

    @Test
    void testCreateShortUrl_NoAvailableHashes() {
        String url = "https://example.com";
        long userId = 1L;

        when(urlRepository.findByUrl(url)).thenReturn(Optional.empty());
        when(hashRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> urlService.createShortUrl(url, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No available hashes");

        verify(hashRepository, times(1)).findAll();
    }
}