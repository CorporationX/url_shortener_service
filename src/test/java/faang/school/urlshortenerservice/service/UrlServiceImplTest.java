package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    private final String prefix = "https://";

    private UrlServiceImpl urlService;

    @BeforeEach
    void setUp() {
        urlService = new UrlServiceImpl(urlRepository, urlCacheRepository, hashCache, prefix);
    }

    @Test
    public void whenTestGetHashFromUrlThenHash() {
        String baseUrl = "https://www.example.com";
        UrlDto urlDto = new UrlDto(baseUrl);
        when(urlRepository.findByBaseUrl(baseUrl)).thenReturn(Optional.of(new Url("abc123", baseUrl, LocalDateTime.now())));
        String hash = urlService.getHashFromUrl(urlDto);
        assertEquals(prefix + "abc123", hash);
    }

    @Test
    public void testGetUrlFromHashThenUrl() {
        String hash = "abc123";
        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(Optional.of("https://www.example.com"));
        String url = urlService.getUrlFromHash(hash);
        assertEquals("https://www.example.com", url);
    }
}