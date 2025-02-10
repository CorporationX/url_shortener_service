package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Base62EncoderTest {

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    void shouldEncodeSingleNumberCorrectly() {
        List<Long> numbers = List.of(1L);

        List<String> encoded = base62Encoder.encodeBatch(numbers);

        assertEquals(1, encoded.size());
        assertEquals("1", encoded.get(0));
    }

    @Test
    void shouldEncodeMultipleNumbersCorrectly() {
        List<Long> numbers = List.of(1L, 62L, 3844L);

        List<String> encoded = base62Encoder.encodeBatch(numbers);

        assertEquals(3, encoded.size());
        assertEquals("1", encoded.get(0));
        assertEquals("10", encoded.get(1));
        assertEquals("100", encoded.get(2));
    }

    @Test
    void shouldReturnEmptyListForEmptyInput() {
        List<Long> numbers = List.of();

        List<String> encoded = base62Encoder.encodeBatch(numbers);

        assertTrue(encoded.isEmpty());
    }

    @Test
    void shouldEncodeLargeNumbersCorrectly() {
        List<Long> numbers = List.of(123456789L, 999999999L);

        List<String> encoded = base62Encoder.encodeBatch(numbers);

        assertEquals(2, encoded.size());
        assertEquals("8M0kX", encoded.get(0));
        assertEquals("15ftgF", encoded.get(1));
    }

    @Test
    void shouldHandleZeroCorrectly() {
        List<Long> numbers = List.of(0L);

        List<String> encoded = base62Encoder.encodeBatch(numbers);

        assertEquals(1, encoded.size());
        assertEquals("", encoded.get(0));
    }
}