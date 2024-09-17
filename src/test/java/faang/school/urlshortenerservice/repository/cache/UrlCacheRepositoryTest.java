package faang.school.urlshortenerservice.repository.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlCacheRepositoryTest {

    @Mock
    private RedisTemplate<String, String> mockRedisTemplate;

    @InjectMocks
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private ValueOperations<String, String> mockValueOperations;

    private final int hoursToExpire = 24;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlCacheRepository, "hoursToExpire", hoursToExpire);
        when(mockRedisTemplate.opsForValue()).thenReturn(mockValueOperations);
    }

    @Test
    void testSave() {
        String hash = "1";
        String url = "2";
        urlCacheRepository.save(hash, url);

        verify(mockRedisTemplate, times(1)).opsForValue();
        verify(mockValueOperations, times(1)).set(hash, url, hoursToExpire, TimeUnit.HOURS);
    }

    @Test
    void testSave_throws_catches() {
        String hash = "1";
        String url = "2";

        doThrow(new RuntimeException())
                .when(mockValueOperations).set(hash, url, hoursToExpire, TimeUnit.HOURS);

        urlCacheRepository.save(hash, url);

        verify(mockRedisTemplate, times(1)).opsForValue();
        verify(mockValueOperations, times(1)).set(hash, url, hoursToExpire, TimeUnit.HOURS);
    }

    @Test
    void testGetUrl() {
        String hash = "1";
        String url = "2";
        when(mockValueOperations.get(hash)).thenReturn(url);

        Optional<String> result = urlCacheRepository.getUrl(hash);

        assertEquals(url, result.orElse(null));
        verify(mockRedisTemplate, times(1)).opsForValue();
        verify(mockValueOperations, times(1)).get(hash);
    }

    @Test
    void testGetUrl_throws_emptyOptional() {
        String hash = "1";
        doThrow(new RuntimeException())
                .when(mockValueOperations).get(hash);

        Optional<String> result = urlCacheRepository.getUrl(hash);

        assertTrue(result.isEmpty());
        verify(mockRedisTemplate, times(1)).opsForValue();
        verify(mockValueOperations, times(1)).get(hash);
    }
}