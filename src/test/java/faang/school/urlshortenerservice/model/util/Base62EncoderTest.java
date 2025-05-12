package faang.school.urlshortenerservice.model.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {

    private Base62Encoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new Base62Encoder();
        ReflectionTestUtils.setField(
                encoder, "BASE62", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        ReflectionTestUtils.setField(encoder, "base62Length", 62);
    }

    @Test
    void testEncodeNumber_Zero_ReturnsEmptyString() {
        long number = 0;

        String result = encoder.encodeNumber(number);

        assertEquals("", result);
    }

    @Test
    void testEncodeNumber_SmallNumber_ReturnsCorrectBase62() {
        long number = 61;

        String result = encoder.encodeNumber(number);

        assertEquals("z", result);
    }
}