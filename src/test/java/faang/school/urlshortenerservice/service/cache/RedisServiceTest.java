package faang.school.urlshortenerservice.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisServiceTest {

    @Mock
    private RedisTemplate<String, TestClass> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueOperations<String, TestClass> valueOperations;

    @InjectMocks
    private RedisService<TestClass> redisService;

    private String key;
    private Duration duration;

    @BeforeEach
    public void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        key = "testKey";
        duration = Duration.ofSeconds(60);
    }

    @Test
    public void testPut_withTtl() {
        TestClass value = new TestClass();

        redisService.put(key, value, duration);

        verify(valueOperations).set(key, value, duration);
    }

    @Test
    public void testGetValue_existingKey() {
        TestClass expectedValue = new TestClass("testName");
        when(valueOperations.get(key)).thenReturn(expectedValue);
        when(objectMapper.convertValue(expectedValue, TestClass.class)).thenReturn(expectedValue);

        Optional<TestClass> result = redisService.getValue(key, TestClass.class);

        assertTrue(result.isPresent());
        assertEquals(expectedValue, result.get());
    }

    @Test
    public void testGetValue_nonExistingKey() {
        when(valueOperations.get(key)).thenReturn(null);

        Optional<TestClass> result = redisService.getValue(key, TestClass.class);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetExpire_existingKey() {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        long expectedExpire = 60L;
        when(redisTemplate.getExpire(key, timeUnit)).thenReturn(expectedExpire);

        long result = redisService.getExpire(key, timeUnit);

        assertEquals(expectedExpire, result);
    }

    @Test
    public void testGetExpire_nonExistingKey() {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        when(redisTemplate.getExpire(key, timeUnit)).thenReturn(null);

        long result = redisService.getExpire(key, timeUnit);

        assertEquals(0L, result);
    }

    @Test
    public void testDelete_existingKey() {
        when(redisTemplate.delete(key)).thenReturn(true);

        boolean result = redisService.delete(key);

        assertTrue(result);
    }

    @Test
    public void testDelete_nonExistingKey() {
        when(redisTemplate.delete(key)).thenReturn(false);

        boolean result = redisService.delete(key);

        assertFalse(result);
    }

    @Test
    public void testDelete_nullReturn() {
        when(redisTemplate.delete(key)).thenReturn(null);

        boolean result = redisService.delete(key);

        assertFalse(result);
    }

    @Test
    public void testIncrementAndGet_success() {
        long correctResult = 1L;
        when(valueOperations.increment(key)).thenReturn(correctResult);

        long result = redisService.incrementAndGet(key);

        assertEquals(correctResult, result);
    }

    @Test
    public void testIncrementAndGet_nullReturn() {
        long correctResult = 0L;
        when(valueOperations.increment(key)).thenReturn(null);

        long result = redisService.incrementAndGet(key);

        assertEquals(correctResult, result);
    }

    @Test
    public void testAddExpire_success() {
        long currentTtl = 1000L;
        long correctResult = currentTtl + duration.toMillis();
        when(redisTemplate.getExpire(key, TimeUnit.MILLISECONDS)).thenReturn(currentTtl);

        long result = redisService.addExpire(key, duration);

        assertEquals(correctResult, result);
        verify(redisTemplate).expire(key, correctResult, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testAddExpire_noCurrentTtl() {
        long correctResult = duration.toMillis() - 1;
        when(redisTemplate.getExpire(key, TimeUnit.MILLISECONDS)).thenReturn(-1L);

        long result = redisService.addExpire(key, duration);

        assertEquals(correctResult, result);
        verify(redisTemplate).expire(key, correctResult, TimeUnit.MILLISECONDS);
    }

    @Data
    private static class TestClass {
        private String name;

        public TestClass() {}

        public TestClass(String name) {
            this.name = name;
        }
    }
}