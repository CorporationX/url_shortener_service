package faang.school.urlshortenerservice.hash_generator;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class Base62EncoderTest {
    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    public void testEncodeZero() {
        var input = List.of(0L);
        var expected = List.of("0");

        assertEquals(expected, encoder.encode(input));
    }

    @Test
    public void testEncodeSingleDigit() {
        var input = List.of(5L);
        var expected = List.of("5");

        assertEquals(expected, encoder.encode(input));
    }

    @Test
    public void testEncodeMultipleDigits() {
        var input = List.of(12345L);
        var expected = List.of("7D3");

        assertEquals(expected, encoder.encode(input));
    }

    @Test
    public void testEncodeMultipleNumbers() {
        var input = List.of(0L, 10L, 62L);
        var expected = List.of("0", "A", "01");

        assertEquals(expected, encoder.encode(input));
    }

    @Test
    public void testEncodeEmptyList() {
        List<Long> input = List.of();
        List<String> expected = List.of();

        assertEquals(expected, encoder.encode(input));
    }

    @Test
    void testEncodeLargeNumber() {
        List<Long> input = List.of(Long.MAX_VALUE);

        var result = encoder.encode(input);

        assertFalse(result.isEmpty());
        var encodedValue = result.get(0);
        for (char c : encodedValue.toCharArray()) {
            assertTrue(Base62Encoder.BASE62_CHARS.indexOf(c) >= 0);
        }
    }

    @Test
    void testNoDuplicatesInOutput() {
        var input = List.of(1L, 2L, 10L, 62L, 100L, 1000L);

        var result = encoder.encode(input);

        assertEquals(input.size(), result.stream().distinct().count());
        assertEquals(input.size(), Set.copyOf(result).size());
    }
}