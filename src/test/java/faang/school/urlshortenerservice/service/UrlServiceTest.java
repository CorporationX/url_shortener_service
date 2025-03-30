package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.entity.UrlMapping;
import faang.school.urlshortenerservice.repository.RedisShortenerRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashService hashService;

    @Mock
    private RedisShortenerRepository redisShortenerRepository;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(urlService, "maxTtlSeconds", 3600);
    }

    @Test
    void generateShortUrl_shouldSaveMappingAndReturnHash() {
        String longUrl = "https://example.com";
        int ttl = 1200;
        String hash = "abc123";
        FreeHash freeHash = new FreeHash(hash);
        UrlMapping expectedMapping = new UrlMapping(hash, longUrl, LocalDateTime.now(), LocalDateTime.now().plusSeconds(ttl));

        when(hashService.getAvailableHash()).thenReturn(freeHash);
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(expectedMapping);

        FreeHash result = urlService.generateShortUrl(longUrl, ttl);

        assertEquals(hash, result.getHash());
        verify(redisShortenerRepository).saveShortUrl(eq(hash), eq(longUrl), eq((long) ttl));
        verify(urlRepository).save(any(UrlMapping.class));
    }

    @Test
    void generateShortUrl_shouldThrowIfTtlTooHigh() {
        int ttl = 999999;
        assertThrows(IllegalArgumentException.class, () ->
                urlService.generateShortUrl("https://example.com", ttl));
    }

    @Test
    void resolveLongUrl_shouldReturnFromRedisIfPresent() {
        String hash = "abc123";
        String longUrl = "https://example.com";

        when(redisShortenerRepository.getLongUrl(hash)).thenReturn(longUrl);

        String result = urlService.resolveLongUrl(hash);

        assertEquals(longUrl, result);
        verify(redisShortenerRepository).getLongUrl(hash);
        verify(urlRepository, never()).findByHashThrow(any());
    }

    @Test
    void resolveLongUrl_shouldLoadFromDbAndSaveToRedis() {
        String hash = "abc123";
        String longUrl = "https://example.com";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = now.plusMinutes(10);
        UrlMapping mapping = new UrlMapping(hash, longUrl, now, expire);

        when(redisShortenerRepository.getLongUrl(hash)).thenReturn(null);
        when(urlRepository.findByHashThrow(hash)).thenReturn(mapping);

        String result = urlService.resolveLongUrl(hash);

        assertEquals(longUrl, result);
        verify(redisShortenerRepository).saveShortUrl(eq(hash), eq(longUrl), anyLong());
    }

    @Test
    void resolveLongUrl_shouldThrowIfExpired() {
        String hash = "expired123";
        String longUrl = "https://expired.com";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusMinutes(1);
        UrlMapping mapping = new UrlMapping(hash, longUrl, now.minusHours(1), expired);

        when(redisShortenerRepository.getLongUrl(hash)).thenReturn(null);
        when(urlRepository.findByHashThrow(hash)).thenReturn(mapping);

        assertThrows(IllegalStateException.class, () -> urlService.resolveLongUrl(hash));
    }
}