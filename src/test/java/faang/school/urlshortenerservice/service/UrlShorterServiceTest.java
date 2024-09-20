package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCache;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotExistException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.DisplayName;
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
public class UrlShorterServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlCache urlCache;
    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlShorterService urlShorterService;

    private final static String longUrl = "https://extra-ultra-mega-super-puper-long-url.com";

    private final static String shortUrl = "http://localhost:8000/hf3j6nsg";

    @Test
    @DisplayName("Test create short URL")
    public void testCreateShortUrl() {

        when(hashCache.getHash()).thenReturn(
                shortUrl
        );

        when(urlRepository.save(
                Url.builder()
                        .url(longUrl)
                        .shortUrl(shortUrl)
                        .build()
        )).thenReturn(
                Url.builder()
                        .id(1L)
                        .url(longUrl)
                        .shortUrl(shortUrl)
                        .build()
        );

        ShortUrlDto expected = new ShortUrlDto(1L, shortUrl);
        ShortUrlDto actual = urlShorterService.shortenUrl(longUrl);

        verify(urlCache, times(1)).saveUrl(shortUrl, longUrl);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test get original URL")
    public void testGetOriginalUrl() {
        when(urlCache.getUrl(shortUrl)).thenReturn(Optional.of(longUrl));

        String expected = longUrl;
        String actual = urlShorterService.getOriginalUrl(shortUrl);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test attempt getting unknown URL")
    public void testGetUnknownUrl() {
        when(urlCache.getUrl(shortUrl)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlShorterService.getOriginalUrl(shortUrl));
    }

    @Test
    @DisplayName("Test update existing URL")
    public void testUpdateExistingUrl() {

        Url realUrl = Url.builder()
                .id(1L)
                .url("https://ebatb-kakay-dlinnay-ssulka.com")
                .shortUrl("shortUrl")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Url spyUrl = spy(realUrl);

        when(urlRepository.findById(1L)).thenReturn(Optional.of(spyUrl));

        String newUrl = "https://updated-url.com";

        urlShorterService.updateUrl(1L, newUrl);

        verify(spyUrl, times(1)).setUrl(newUrl);
        verify(spyUrl, times(1)).setUpdatedAt(any(LocalDateTime.class));

        verify(urlCache, times(1)).saveUrl(spyUrl.getShortUrl(), newUrl);
    }

    @Test
    @DisplayName("Test attempt update non existent URL")
    public void testUpdateNonExistentUrl() {
        when(urlRepository.findById(-1L)).thenReturn(Optional.empty());
        assertThrows(UrlNotExistException.class, () -> urlShorterService.updateUrl(-1L, longUrl));
    }
}
