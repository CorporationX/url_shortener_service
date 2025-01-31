package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Base62EncoderTest {

    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        base62Encoder = new Base62Encoder(alphabet);
    }

    @Test
    @DisplayName("Encode number")
    void test_encode_below61_success() {
        Long number1 = 1L;
        Long number0 = 0L;
        Long number10 = 10L;
        Long number61 = 61L;
        Long number62 = 62L;
        Long number125 = 125L;

        String result1 = base62Encoder.encode(number1);
        String result0 = base62Encoder.encode(number0);
        String result10 = base62Encoder.encode(number10);
        String result61 = base62Encoder.encode(number61);
        String result62 = base62Encoder.encode(number62);
        String result125 = base62Encoder.encode(number125);

        assertNotNull(result1);
        assertNotNull(result0);
        assertNotNull(result10);
        assertNotNull(result61);
        assertEquals("1", result1);
        assertEquals("0", result0);
        assertEquals("A", result10);
        assertEquals("z", result61);
        assertEquals("01", result62);
        assertEquals("12", result125);
    }

}