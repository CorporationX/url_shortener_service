package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private HashService hashService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Value("${api.base-url}")
    private String baseUrl;

    private UrlDto urlDto;
    private String hash;
    private String originalUrl;
    private Url url;

    @BeforeEach
    public void setUp() {
        urlDto = UrlDto.builder()
                .url("https://example.com")
                .build();
        hash = "abc123";
        originalUrl = "https://example.com/api/v1/abv";
        url = Url.builder()
                .url(originalUrl)
                .hash(hash)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testShortenUrl() {
        when(hashCache.getHash()).thenReturn(hash);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        UrlDto result = urlService.shortenUrl(urlDto);

        verify(urlRepository, times(1)).save(any(Url.class));
        assertEquals(baseUrl + "/abc123", result.url());
    }

    @Test
    public void testGetOriginalUrl() {
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
    }

    @Test
    public void testGetOriginalUrlNotFound() {
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> urlService.getOriginalUrl(hash));
    }

    @Test
    public void testDeleteOldRecordsAndSaveHashes() {
        List<String> oldHashes = List.of("hash1", "hash2");
        when(urlRepository.deleteOldRecordsAndReturnHashes()).thenReturn(oldHashes);

        urlService.deleteOldRecordsAndSaveHashes();

        verify(urlRepository, times(1)).deleteOldRecordsAndReturnHashes();
        verify(hashService, times(1)).saveHashes(anyList());
    }
}
