package faang.school.urlshortenerservice.service;

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
    void testCreateShortUrl() {
        when(urlRepository.findByUrl("https://example.com")).thenReturn(Optional.empty());

        when(hashRepository.findAll()).thenReturn(List.of(new faang.school.urlshortenerservice.entity.Hash("abc123")));

        when(urlRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        long userId = 1L;
        String shortUrl = urlService.createShortUrl("https://example.com", userId);

        assertThat(shortUrl).isEqualTo("abc123");
        verify(urlRepository, times(1)).save(any());
    }
}