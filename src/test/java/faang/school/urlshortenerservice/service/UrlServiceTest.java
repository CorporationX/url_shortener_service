package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @InjectMocks
    UrlService urlService;

    @Mock
    UrlRepository urlRepository;

    @Mock
    UrlCacheRepository urlCacheRepository;

    @Mock
    LocalCache localCache;

    @Captor
    ArgumentCaptor<Url> captor;

    @Test
    void createShortUrl_shouldSaveLongUrlAndReturnHash() {
        UrlRequestDto urlDto = new UrlRequestDto("https://testing");
        String expectedHash = "jp34N6";

        when(localCache.getHash()).thenReturn(expectedHash);

        String actualHash = urlService.createShortUrl(urlDto);

        verify(urlRepository, times(1)).save(captor.capture());
        verify(urlCacheRepository, times(1)).save(expectedHash, urlDto.url());

        Url capturedUrl = captor.getValue();

        assertEquals(urlDto.url(), capturedUrl.getUrl());
        assertEquals(expectedHash, capturedUrl.getHash());
        assertThat(capturedUrl.getCreatedAt()).isNotNull();

        assertEquals(expectedHash, actualHash);
    }

    @Test
    void createShortUrl_shouldPropagate_whenLocalCacheEmpty() {
        when(localCache.getHash()).thenThrow(
                new IllegalStateException("hash cache is empty")
        );

        assertThrows(IllegalStateException.class,
                () -> urlService.createShortUrl(new UrlRequestDto("")));

        verify(urlRepository, never()).save(any());
        verify(urlCacheRepository, never()).save(any(), any());
    }

    @Test
    void getOriginalUrl_shouldReturnUrlFromCache() {
        String hash = "3Ff0cv";
        String expectedUrl = "https://tesssting";

        when(urlCacheRepository.getUrl(hash))
                .thenReturn(Optional.of(expectedUrl));

        String actualUrl = urlService.getOriginalUrl(hash);

        assertEquals(expectedUrl, actualUrl);

        verify(urlCacheRepository, times(1)).getUrl(hash);
        verify(urlRepository, never()).findByHash(any());
    }

    @Test
    void getOriginalUrl_shouldReturnUrlFromDB() {
        String hash = "3Ff0cv";
        String expectedUrl = "https://tesssting";
        Url url = new Url(hash, expectedUrl, Instant.now());

        when(urlCacheRepository.getUrl(hash))
                .thenReturn(Optional.empty());

        when(urlRepository.findByHash(hash))
                .thenReturn(Optional.of(url));

        String actualUrl = urlService.getOriginalUrl(hash);

        assertEquals(expectedUrl, actualUrl);

        verify(urlCacheRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(1)).findByHash(hash);
    }

    @Test
    void getOriginalUrl_shouldPropagate_whenUrlNotFound() {
        String hash = "7BFz12";

        when(urlCacheRepository.getUrl(hash))
                .thenReturn(Optional.empty());

        when(urlRepository.findByHash(hash))
                .thenReturn(Optional.empty());

        assertThrows(
                UrlNotFoundException.class,
                () -> urlService.getOriginalUrl(hash)
        );
    }
}
