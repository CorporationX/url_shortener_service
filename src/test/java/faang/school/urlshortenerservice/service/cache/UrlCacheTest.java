package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlCacheTest {
    private static final String PREFIX = "urls:";
    private int ttl = 86400;
    @Mock
    private StringRedisTemplate cache;
    @Mock
    private UrlRepository urlRepository;
    @InjectMocks
    private UrlCacheImpl urlCache;

    @Test
    void testGetFromCache() {
        String hash = "testHash";
        String url = "http://example.com";

        when(cache.opsForValue().get(anyString())).thenReturn(url);

        String result = urlCache.get(hash);

        assertEquals(url, result);
        verify(cache.opsForValue()).get(PREFIX + hash);
        verify(urlRepository, never()).findByIdOrThrow(anyString());
    }

    @Test
    void testGetFromDB() {
        String hash = "testHash";
        String url = "http://example.com";

        when(cache.opsForValue().get(anyString())).thenReturn(null);
        when(urlRepository.findByIdOrThrow(hash)).thenReturn(mockUrl(url));

        String result = urlCache.get(hash);

        assertEquals(url, result);
        verify(urlRepository).findByIdOrThrow(hash);
        verify(cache.opsForValue()).set(anyString(), eq(url), anyInt(), any());
    }

    @Test
    void testGetNotFound() {
        String hash = "testHash";

        when(cache.opsForValue().get(anyString())).thenReturn(null);
        when(urlRepository.findByIdOrThrow(hash)).thenThrow(new ResourceNotFoundException("Not found"));

        assertThrows(ResourceNotFoundException.class, () -> urlCache.get(hash));
    }

    @Test
    void testSet() {
        String hash = "testHash";
        String url = "http://example.com";

        urlCache.set(hash, url);

        verify(cache.opsForValue()).set(
                eq(PREFIX + hash),
                eq(url),
                eq(ttl),
                eq(TimeUnit.SECONDS)
        );
    }

    @Test
    void testDelete() {
        String hash = "testHash";

        urlCache.delete(hash);

        verify(cache.opsForValue().getOperations()).delete(PREFIX + hash);
    }

    @Test
    void testDeleteAll() {
        List<String> hashes = List.of("hash1", "hash2", "hash3");

        urlCache.deleteAll(hashes);

        verify(cache.opsForValue().getOperations()).delete(
                List.of(
                        PREFIX + "hash1",
                        PREFIX + "hash2",
                        PREFIX + "hash3"
                )
        );
    }

    private Url mockUrl(String url) {
        Url mockUrl = mock(Url.class);
        when(mockUrl.getUrl()).thenReturn(url);
        return mockUrl;
    }
}