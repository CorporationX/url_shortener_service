package faang.school.urlshortenerservice.service.search.url;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisUrlServiceTest {

    @InjectMocks
    private RedisUrlService redisUrlService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private String hash;
    private String url;

    @BeforeEach
    public void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        hash = "abc123";
        url = "http://example.com";
    }

    @Test
    public void testFindUrlWhenHashExists() {
        when(valueOperations.get(hash)).thenReturn(url);

        Optional<String> result = redisUrlService.findUrl(hash);

        assertTrue(result.isPresent());
        assertEquals(url, result.get());
    }

    @Test
    public void testFindUrlWhenHashDoesNotExist() {
        when(valueOperations.get(hash)).thenReturn(null);

        Optional<String> result = redisUrlService.findUrl(hash);

        assertFalse(result.isPresent());
    }
}
