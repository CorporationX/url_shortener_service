package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    void encode() {
        long num = 1_000_000;
        String expected = "29C4";
        assertEquals(expected, base62Encoder.encode(num));
    }

    @Test
    void encode_ShouldEmptyEncodeWhenNumIsNegative() {
        long num = 0;
        String expected = "";
        assertEquals(expected, base62Encoder.encode(num));
    }
}