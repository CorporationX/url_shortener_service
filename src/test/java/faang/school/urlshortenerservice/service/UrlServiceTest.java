package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.dto.CreateUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import faang.school.urlshortenerservice.service.cache.UrlCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private HashCache cache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCache urlCache;
    @InjectMocks
    private UrlServiceImpl service;

    @Test
    void testCreateShortUrl_Success() {
        CreateUrlDto dto = new CreateUrlDto("https://example.com");
        String testHash = "abc123";
        Url testUrl = Url.builder()
                .url(dto.originalUrl())
                .hash(testHash)
                .build();

        when(cache.getHash()).thenReturn(testHash);
        when(urlRepository.save(testUrl)).thenReturn(testUrl);

        String result = service.createShortUrl(dto);

        assertEquals("http://localhost:8080/abc123", result);
        verify(cache).getHash();
        verify(urlRepository).save(testUrl);
        verify(urlCache).set(testHash, dto.originalUrl());
    }

    @Test
    void testCreateShortUrl_CacheEmpty() {
        CreateUrlDto dto = new CreateUrlDto("https://example.com");

        when(cache.getHash()).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            service.createShortUrl(dto);
        });
    }

    @Test
    void testGetOriginalUrl_FromCache() {
        String hash = "abc123";
        String originalUrl = "https://example.com";

        when(urlCache.get(hash)).thenReturn(originalUrl);

        String result = service.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlCache).get(hash);
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        String hash = "abc123";

        when(urlCache.get(hash)).thenReturn(null);

        String result = service.getOriginalUrl(hash);

        assertNull(result);
        verify(urlCache).get(hash);
    }
}