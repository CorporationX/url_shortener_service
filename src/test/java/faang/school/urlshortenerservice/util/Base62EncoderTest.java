package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {
    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
    }

    @Test
    void testEncode_singleNumber() {
        List<Long> inputNumbers = List.of(123L);
        List<String> encoded = base62Encoder.encode(inputNumbers);

        assertEquals(1, encoded.size());
        assertEquals("1z", encoded.get(0));
    }

    @Test
    void testEncode_multipleNumbers() {
        List<Long> inputNumbers = List.of(0L, 456789L, 99999999L);
        List<String> encoded = base62Encoder.encode(inputNumbers);

        assertEquals(3, encoded.size());
        assertEquals("0", encoded.get(0));
        assertEquals("1upZ", encoded.get(1));
        assertEquals("6laZD", encoded.get(2));
    }

    @Test
    void testEncode_emptyList() {
        List<Long> inputNumbers = List.of();
        List<String> encoded = base62Encoder.encode(inputNumbers);

        assertTrue(encoded.isEmpty());
    }
}