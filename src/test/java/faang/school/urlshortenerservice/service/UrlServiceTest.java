package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.Redirect;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private URLCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    @DisplayName("Test get short url")
    void testGetShortUrl() {
        String expectedHash = "abc123";
        String expectedUrl = "https://example.com";
        UrlDto urlDto = new UrlDto(expectedUrl);

        when(hashCache.getHash()).thenReturn(expectedHash);

        Url expectedUrlObject = Url.builder()
                .url(expectedUrl)
                .hash(expectedHash)
                .build();

        when(urlRepository.save(any(Url.class))).thenReturn(expectedUrlObject);

        String actualHash = urlService.getShortUrl(urlDto);

        assertEquals(expectedHash, actualHash);
        verify(urlCacheRepository).saveUrl(expectedUrlObject);
    }

    @Test
    @DisplayName("Test get url")
    void testGetUrl() {
        String expectedHash = "abc123";
        String expectedUrl = "https://example.com";

        when(urlCacheRepository.getUrl(expectedHash)).thenReturn(expectedUrl);

        Redirect redirect = urlService.getUrl(expectedHash);

        assertEquals(expectedUrl, redirect.getUrl());
    }

    @Test
    @DisplayName("Test get url not found")
    void testGetUrlNotFound() {
        String expectedHash = "abc123";
        String expectedUrl = "https://example.com";

        when(urlCacheRepository.getUrl(expectedHash)).thenReturn(null);
        when(urlRepository.findById(expectedHash)).thenReturn(java.util.Optional.of(Url.builder().url(expectedUrl).hash(expectedHash).build()));

        Redirect redirect = urlService.getUrl(expectedHash);

        assertEquals(expectedUrl, redirect.getUrl());
    }

    @Test
    @DisplayName("Test get url not found exception")
    void testGetUrlNotFoundException() {
        String expectedHash = "abc123";

        when(urlCacheRepository.getUrl(expectedHash)).thenReturn(null);
        when(urlRepository.findById(expectedHash)).thenReturn(java.util.Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> urlService.getUrl(expectedHash));
        assertEquals("Url not with hash " + expectedHash + " not found", exception.getMessage());
    }
}
