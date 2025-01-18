package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {
    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    public void testEncode() {
        long number = 123456789089898L;

        String result = base62Encoder.encode(number);
        assertEquals("2GxXBw3z", result);
    }
}