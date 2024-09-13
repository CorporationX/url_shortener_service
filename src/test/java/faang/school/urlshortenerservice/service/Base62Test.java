package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Base62Test {

    private Base62 base62;

    @BeforeEach
    void setUp() {
        base62 = new Base62();
    }

    @Test
    void testEncodeZero() {
        String encoded = base62.encode(0L);
        assertEquals("0", encoded, "Encoding 0 should return '0'");
    }

    @Test
    void testEncodePositiveNumber() {
        long input = 125L;
        String encoded = base62.encode(input);
        assertEquals("21", encoded, "Encoding 125 should return '21'");
    }


    @Test
    void testDecodeSingleDigit() {
        String encoded = "1";
        long decoded = base62.decode(encoded);
        assertEquals(1L, decoded, "Decoding '1' should return 1");
    }

    @Test
    void testDecodeMultiDigit() {
        String encoded = "21";
        long decoded = base62.decode(encoded);
        assertEquals(125L, decoded, "Decoding '21' should return 125");
    }

    @Test
    void testEncodeDecodeSymmetry() {
        long input = 123456789L;
        String encoded = base62.encode(input);
        long decoded = base62.decode(encoded);
        assertEquals(input, decoded, "Decoding after encoding should return the original value");
    }
}