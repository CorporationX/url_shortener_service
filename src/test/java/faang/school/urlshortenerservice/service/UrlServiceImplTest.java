package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.RedisCache;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private RedisCache redisCache;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    private final String hash = "exampleHash";
    private final String longUrl = "https://example.com";

    @Test
    public void testCleaner() {
        ReflectionTestUtils.setField(urlService, "urlLifeTime", 8760L);
        LocalDateTime now = LocalDateTime.now().minusHours(8760);

        List<Hash> hashes = List.of(new Hash("exampleHash1"), new Hash("exampleHash2"));
        when(urlRepository.deleteOldUrlsAndReturnHashes(any(LocalDateTime.class))).thenReturn(hashes);

        urlService.cleaningOldHashes();

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(urlRepository, times(1)).deleteOldUrlsAndReturnHashes(captor.capture());

        LocalDateTime capturedDateTime = captor.getValue();

        long diff = Math.abs(capturedDateTime.getHour() - now.getHour());
        assert diff < 1 : "Дата и время не совпадают, разница в часах: " + diff;

        verify(hashRepository, times(1)).saveAll(hashes);
    }

    @Test
    void testGetLongUrlByHash_FoundInCache() {
        when(redisCache.getFromCache(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getLongUrlByHash(hash);

        assertThat(result).isEqualTo(longUrl);
        verify(redisCache).getFromCache(hash);
        verify(urlRepository, never()).findUrlByHash(hash);
    }

    @Test
    void testGetLongUrlByHash_FoundInDatabase() {
        when(redisCache.getFromCache(hash)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getLongUrlByHash(hash);

        assertThat(result).isEqualTo(longUrl);
        verify(redisCache).getFromCache(hash);
        verify(urlRepository).findUrlByHash(hash);
    }

    @Test
    void testGetLongUrlByHash_NotFound() {
        when(redisCache.getFromCache(hash)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(hash)).thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(UrlNotFoundException.class, () -> {
            urlService.getLongUrlByHash(hash);
        });

        assertThat(exception.getMessage()).isEqualTo("URL not found for hash %s".formatted(hash));
        verify(redisCache).getFromCache(hash);
        verify(urlRepository).findUrlByHash(hash);
    }

    @Test
    public void testGetShortUrlByHash() {
        String testUrl = "https://example.com/long-url";
        String generatedHash = "abc123";

        when(hashCache.getHash()).thenReturn(generatedHash);

        String resultHash = urlService.getShortUrlByHash(testUrl);

        assertEquals(testUrl, resultHash);

        verify(redisCache, times(1)).saveToCache(generatedHash, testUrl);

        Url expectedUrl = Url.builder()
                .hash(generatedHash)
                .url(testUrl)
                .build();
        verify(urlRepository, times(1)).save(expectedUrl);
    }
}