package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.url.CreateUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.url.UrlServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    private final String baseUrl = "http://127.0.0.1:8080";

    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlServiceImp urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", baseUrl);
    }

    @Test
    void testCreateShortUrl_ValidUrl_ShouldSaveAndReturnShortUrl() {
        CreateUrlDto dto = new CreateUrlDto("https://example.com/path");
        when(hashCache.getHash()).thenReturn("abc123");

        String result = urlService.createShortUrl(dto);

        assertEquals(baseUrl + "/abc123", result);

        verify(urlRepository).save(argThat(url -> url.getHash().equals("abc123") &&
                url.getUrl().equals("https://example.com/path")
        ));
        verify(urlCacheRepository).save("abc123", "https://example.com/path");
    }

    @Test
    void testeGetOriginalUrl_WhenInCache_ShouldReturnFromCache() {
        when(urlCacheRepository.get("abc123")).thenReturn(Optional.of("https://example.com"));

        String result = urlService.getOriginalUrl("abc123");

        assertEquals("https://example.com", result);
        verify(urlRepository, never()).getByHashOrThrow(any());
    }

    @Test
    void testGetOriginalUrl_WhenNotInCache_ShouldReturnFromDb() {
        when(urlCacheRepository.get("abc123")).thenReturn(Optional.empty());
        when(urlRepository.getByHashOrThrow("abc123"))
                .thenReturn(Url.builder()
                        .hash("abc123")
                        .url("https://example.com")
                        .build());

        String result = urlService.getOriginalUrl("abc123");

        assertEquals("https://example.com", result);
        verify(urlCacheRepository).get("abc123");
        verify(urlRepository).getByHashOrThrow("abc123");
    }

    @Test
    void testCleanExpiredHashes_ShouldMoveHashesToHashRepository() {
        List<String> expiredHashes = List.of("hash1", "hash2", "hash3");
        when(urlRepository.getHashBatchAndDelete(1)).thenReturn(expiredHashes);

        urlService.cleanExpiredHashes(1);

        verify(urlRepository).getHashBatchAndDelete(1);
        verify(hashRepository).saveHashes(expiredHashes);
    }
}