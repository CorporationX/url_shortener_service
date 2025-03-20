package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlShortDto;
import faang.school.urlshortenerservice.cache.UrlCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCache urlCache;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    @Test
    void testCreateShortUrl() {
        UrlDto urlDto = new UrlDto("https://example.com");
        when(hashCache.getHash()).thenReturn("abc123");
        doNothing().when(urlRepository).save(urlDto.url(), "abc123");
        doNothing().when(urlCache).save(urlDto.url(), "abc123");

        UrlShortDto result = urlService.createShortUrl(urlDto);

        assertNotNull(result);
        assertEquals("https://short.url/abc123", result.shortUrl());
        verify(hashCache).getHash();
        verify(urlRepository).save(urlDto.url(), "abc123");
        verify(urlCache).save(urlDto.url(), "abc123");
    }

}