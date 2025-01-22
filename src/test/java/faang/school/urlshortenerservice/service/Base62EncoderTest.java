package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Base62EncoderTest {

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    void encodeToBase62_shouldReturnCorrectBase62Strings() {
        List<Long> input = Arrays.asList(1L, 10L, 61L, 62L, 123456789L, 0L);
        List<String> expected = Arrays.asList("000001", "00000A", "00000z", "000010", "08M0kX", "000000");

        List<String> result = base62Encoder.encode(input);

        assertEquals(expected, result);
    }

    @Test
    void encodeToBase62_shouldThrowExceptionForNullInput() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> base62Encoder.encode(null));
        assertEquals("List must not be null or empty.", exception.getMessage());
    }

    @Test
    void encodeToBase62_shouldReturnEmptyListForEmptyInput() {
        List<Long> input = Collections.emptyList();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> base62Encoder.encode(input));
        assertEquals("List must not be null or empty.", exception.getMessage());
    }

    @Test
    void encodeToBase62_shouldThrowExceptionForNegativeInput() {
        List<Long> input = Arrays.asList(-1L, 10L, 0L);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> base62Encoder.encode(input));
        assertEquals("List must contain only Long values.", exception.getMessage());
    }
}