package faang.school.urlshortenerservice.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Base62EncoderTest {

    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
    }

    @Test
    void testEncodeNumberWithZero() {
        String result = ReflectionTestUtils.invokeMethod(base62Encoder, "encodeNumber", 0L);
        assertEquals("0", result, "Encoding 0 should return '0'");
    }

    @Test
    void testEncodeNumberWithSingleDigit() {
        String result = ReflectionTestUtils.invokeMethod(base62Encoder, "encodeNumber", 9L);
        assertEquals("9", result, "Encoding 9 should return '9'");
    }

    @Test
    void testEncodeNumberWithBase62Characters() {
        String result1 = ReflectionTestUtils.invokeMethod(base62Encoder, "encodeNumber", 62L);
        String result2 = ReflectionTestUtils.invokeMethod(base62Encoder, "encodeNumber", 123L);

        assertEquals("10", result1, "Encoding 62 should return '10'");
        assertEquals("1z", result2, "Encoding 123 should return '1z'");
    }

    @Test
    void testEncodeNumberForLargeValues() {
        String result = ReflectionTestUtils.invokeMethod(base62Encoder, "encodeNumber", 9876543210L);
        assertTrue(result.length() > 1, "Encoded large numbers should have multiple characters");
    }

    @Test
    void testEncodeEmptyList() {
        List<Long> numbers = List.of();
        List<String> result = base62Encoder.encode(numbers);

        assertTrue(result.isEmpty(), "Encoding an empty list should return an empty list");
    }
}

