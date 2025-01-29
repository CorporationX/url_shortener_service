package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class Base62EncoderTest {
    private Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    void testEncodeRepeat() {
        long input = 4;

        String firstResult = base62Encoder.encode(input);
        String secondResult = base62Encoder.encode(input);
        assertEquals(firstResult, secondResult);
    }

    @Test
    void testEncodeWithOne() {
        long input = 1L;
        String expected = "1";

        String actual = base62Encoder.encode(input);
        assertEquals(expected, actual);
    }

    @Test
    void testEncodeWithZero() {
        long input = 0L;

        assertThrows(IllegalArgumentException.class, () -> base62Encoder.encode(input));
    }

    @Test
    void testEncodeWithNegativeNumber() {
        long input = -1L;

        assertThrows(IllegalArgumentException.class, () -> base62Encoder.encode(input));
    }

}
