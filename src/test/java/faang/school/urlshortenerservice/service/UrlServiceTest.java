package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    HashCache hashCache;
    @Mock
    CacheManager cacheManager;
    @Mock
    Cache cache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "rootPath", "http://localhost/");
        ReflectionTestUtils.setField(urlService, "urlRetentionPeriod", 6);
    }

    @Test
    void testGetOriginalUrl_Success() {
        String hash = "hash";
        String originalUrl = "http://example.com";
        Url url = getUrl(hash, originalUrl);

        when(urlRepository.findById(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlRepository, times(1)).findById(hash);
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        String hash = "hash";

        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> urlService.getOriginalUrl(hash));
        verify(urlRepository, times(1)).findById(hash);
    }

    @Test
    void testCreateShortUrl_Success() {
        String hash = "hash";
        String originalUrl = "http://example.com";
        UrlDto urlDto = getUrlDto(originalUrl);

        when(cacheManager.getCache("urlCache")).thenReturn(cache);
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(Mockito.any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrlDto result = urlService.createShortUrl(urlDto);

        assertNotNull(result);
        assertTrue(result.getUrl().contains(hash));
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(Mockito.any(Url.class));
        verify(cache, times(1)).put(hash, originalUrl);
    }

    @Test
    void testGetExpiredHashAndDeleteUrl_Success() {
        List<String> expiredHashes = List.of("hash1", "hash2");

        when(urlRepository.getExpiredHashAndDeleteUrl()).thenReturn(expiredHashes);

        List<String> result = urlService.getExpiredHashAndDeleteUrl();

        assertEquals(expiredHashes, result);
        verify(urlRepository, times(1)).getExpiredHashAndDeleteUrl();
    }

    @Test
    void testValidateExpiresAt_NullExpiresAt() {
        LocalDateTime now = LocalDateTime.now();
        UrlDto urlDto = UrlDto.builder()
                .expiresAt(null)
                .build();

        LocalDateTime result = urlService.validateExpiresAt(urlDto, now);

        assertEquals(now.plusMonths(getUrlRetentionPeriod()), result);
    }

    @Test
    void testValidateExpiresAt_ValidExpiresAt() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMonths(2);
        UrlDto urlDto = UrlDto.builder()
                .expiresAt(expiresAt)
                .build();

        LocalDateTime result = urlService.validateExpiresAt(urlDto, now);

        assertEquals(expiresAt, result);
    }

    @Test
    void testPutInCache() {
        String hash = "hash";
        String url = "http://example.com";
        when(cacheManager.getCache("urlCache")).thenReturn(cache);

        urlService.putInCache(hash, url);

        verify(cache, times(1)).put(hash, url);
    }

    public UrlDto getUrlDto(String url) {
        return UrlDto.builder()
                .url(url)
                .build();
    }

    public Url getUrl(String hash, String url) {
        return Url.builder()
                .hash(hash)
                .url(url)
                .build();
    }

    public int getUrlRetentionPeriod(){
        return (int) ReflectionTestUtils.getField(urlService,"urlRetentionPeriod");
    }
}