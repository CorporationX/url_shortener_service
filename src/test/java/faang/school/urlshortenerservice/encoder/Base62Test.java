package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Base62Test {

    private Base62Encoder base62;

    @BeforeEach
    void setUp() {
        base62 = new Base62Encoder();
    }

    @Test
    void testEncodeZero() {
        String encoded = base62.encode(List.of(0L)).get(0).getHash();
        assertEquals("0", encoded, "Encoding 0 should return '0'");
    }

    @Test
    void testEncodePositiveNumber() {
        List<Long> input = List.of(125L);
        String encoded = base62.encode(input).get(0).getHash();
        assertEquals("21", encoded, "Encoding 125 should return '21'");
    }
}