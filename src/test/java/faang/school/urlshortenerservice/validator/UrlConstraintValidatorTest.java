package faang.school.urlshortenerservice.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlConstraintValidatorTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    private UrlConstraintValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UrlConstraintValidator(redisTemplate);
        validator.init();
    }

    @Test
    void testValidUrlNotBlacklisted() {
        String validUrl = "http://example.com";
        when(redisTemplate.hasKey(validUrl)).thenReturn(false);

        boolean result = validator.isValid(validUrl, null);

        assertTrue(result);
    }

    @Test
    void testValidUrlBlacklisted() {
        String validUrl = "https://example.com";
        when(redisTemplate.hasKey(validUrl)).thenReturn(true);

        boolean result = validator.isValid(validUrl, null);

        assertFalse(result);
    }

    @Test
    void testInvalidUrlNotBlacklisted() {
        String invalidUrl = "htp://badurl";
        when(redisTemplate.hasKey(invalidUrl)).thenReturn(false);

        boolean result = validator.isValid(invalidUrl, null);

        assertFalse(result);
    }

    @Test
    void testInvalidUrlBlacklisted() {
        String invalidUrl = "notaurl";
        when(redisTemplate.hasKey(invalidUrl)).thenReturn(true);

        boolean result = validator.isValid(invalidUrl, null);

        assertFalse(result);
    }
}
