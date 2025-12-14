package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UrlCacheRepositoryTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UrlCacheRepository urlCacheRepository;

    private static final String TEST_HASH = "abc123";
    private static final String TEST_URL = "https://example.com/test";
    private static final long TTL = 86400L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlCacheRepository, "cacheTtlSeconds", TTL);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testSaveSuccess() {
        // When
        urlCacheRepository.save(TEST_HASH, TEST_URL);

        // Then
        verify(valueOperations).set(
                eq(TEST_HASH),
                eq(TEST_URL),
                eq(Duration.ofSeconds(TTL))
        );
    }

    @Test
    void testSaveThrowsException() {
        // Given
        doThrow(new RuntimeException("Redis connection error"))
                .when(valueOperations).set(anyString(), anyString(), any(Duration.class));

        // When & Then
        assertThatThrownBy(() -> urlCacheRepository.save(TEST_HASH, TEST_URL))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to save URL to cache");

        verify(valueOperations).set(eq(TEST_HASH), eq(TEST_URL), any(Duration.class));
    }

    @Test
    void testGetSuccess() {
        // Given
        when(valueOperations.get(TEST_HASH)).thenReturn(TEST_URL);

        // When
        String result = urlCacheRepository.get(TEST_HASH);

        // Then
        assertThat(result).isEqualTo(TEST_URL);
        verify(valueOperations).get(TEST_HASH);
    }

    @Test
    void testGetReturnsNullWhenNotFound() {
        // Given
        when(valueOperations.get(TEST_HASH)).thenReturn(null);

        // When
        String result = urlCacheRepository.get(TEST_HASH);

        // Then
        assertThat(result).isNull();
        verify(valueOperations).get(TEST_HASH);
    }

    @Test
    void testGetReturnsNullOnException() {
        // Given
        when(valueOperations.get(TEST_HASH)).thenThrow(new RuntimeException("Redis error"));

        // When
        String result = urlCacheRepository.get(TEST_HASH);

        // Then
        assertThat(result).isNull();
        verify(valueOperations).get(TEST_HASH);
    }

    @Test
    void testDeleteSuccess() {
        // When
        urlCacheRepository.delete(TEST_HASH);

        // Then
        verify(redisTemplate).delete(TEST_HASH);
    }

    @Test
    void testDeleteDoesNotThrowException() {
        // Given
        doThrow(new RuntimeException("Redis error")).when(redisTemplate).delete(anyString());

        // When - should not throw exception, just log
        urlCacheRepository.delete(TEST_HASH);

        // Then
        verify(redisTemplate).delete(TEST_HASH);
    }

    @Test
    void testSaveWithDifferentTtl() {
        // Given
        ReflectionTestUtils.setField(urlCacheRepository, "cacheTtlSeconds", 3600L);

        // When
        urlCacheRepository.save(TEST_HASH, TEST_URL);

        // Then
        verify(valueOperations).set(
                eq(TEST_HASH),
                eq(TEST_URL),
                eq(Duration.ofSeconds(3600L))
        );
    }

    @Test
    void testMultipleSaves() {
        // When
        urlCacheRepository.save("hash1", "url1");
        urlCacheRepository.save("hash2", "url2");
        urlCacheRepository.save("hash3", "url3");

        // Then
        verify(valueOperations, times(3)).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void testMultipleGets() {
        // Given
        when(valueOperations.get("hash1")).thenReturn("url1");
        when(valueOperations.get("hash2")).thenReturn("url2");
        when(valueOperations.get("hash3")).thenReturn(null);

        // When
        String result1 = urlCacheRepository.get("hash1");
        String result2 = urlCacheRepository.get("hash2");
        String result3 = urlCacheRepository.get("hash3");

        // Then
        assertThat(result1).isEqualTo("url1");
        assertThat(result2).isEqualTo("url2");
        assertThat(result3).isNull();
        verify(valueOperations, times(3)).get(anyString());
    }
}