package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlCashRepository urlCashRepository;

    @Mock
    private LocalCasheService localCashe;

    @InjectMocks
    private UrlService urlService;

    @Test
    void testCreateShortUrl() {

        String expectedHash = "abc123";
        String longUrl = "https://example.com";

        when(localCashe.getHash()).thenReturn(expectedHash);

        String result = urlService.createShortUrl(longUrl);

        assertEquals(expectedHash, result);
        verify(localCashe).getHash();
        verify(urlRepository).save(argThat(url ->
                url.getUrl().equals(longUrl) &&
                        url.getHash().equals(expectedHash)
        ));
        verify(urlCashRepository).save(expectedHash, longUrl);
    }

    @Test
    void testGetOriginalUrl_WhenExistsInCache() {

        String hash = "abc123";
        String expectedUrl = "https://example.com";

        when(urlCashRepository.findByHash(hash)).thenReturn(Optional.of(expectedUrl));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(expectedUrl, result);
        verify(urlCashRepository).findByHash(hash);
        verify(urlRepository, never()).findByHash(any());
    }

    @Test
    void testGetOriginalUrl_WhenNotInCacheButInRepository() {

        String hash = "abc123";
        String expectedUrl = "https://example.com";
        Url urlEntity = new Url(hash, expectedUrl);

        when(urlCashRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(urlEntity));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(expectedUrl, result);
        verify(urlCashRepository).save(hash, expectedUrl);
        verify(urlRepository).findByHash(hash);
    }

    @Test
    void testUrlExpirationDateSetCorrectly() {

        String hash = "abc123";
        String longUrl = "https://example.com";

        when(localCashe.getHash()).thenReturn(hash);

        urlService.createShortUrl(longUrl);

        verify(urlRepository).save(argThat(url ->
                url.getDeletedAt().isAfter(LocalDateTime.now().plusDays(29)) &&
                        url.getDeletedAt().isBefore(LocalDateTime.now().plusDays(31))
        ));
    }
}