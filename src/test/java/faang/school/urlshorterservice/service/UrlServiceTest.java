package faang.school.urlshorterservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    private final String emailPattern = "https://short.url/";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "emailPattern", emailPattern);
    }

    @Test
    void createShortUrl_shouldReturnShortUrlAndSaveToRepositories() {
        String longUrl = "https://example.com/some-long-url";
        String hash = "abc123";
        LocalDateTime now = LocalDateTime.now();

        when(hashCache.getHash(longUrl)).thenReturn(hash);

        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);

        String shortUrl = urlService.createShortUrl(longUrl);

        assertEquals(emailPattern + hash, shortUrl);

        verify(hashCache).getHash(longUrl);
        verify(urlRepository).save(urlCaptor.capture());
        verify(urlCacheRepository).save(hash, longUrl);

        Url capturedUrl = urlCaptor.getValue();
        assertEquals(hash, capturedUrl.getHash());
        assertEquals(longUrl, capturedUrl.getUrl());
        assertEquals(now.getDayOfYear(), capturedUrl.getCreatedAt().getDayOfYear());
    }
}