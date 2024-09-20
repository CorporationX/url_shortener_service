package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @BeforeEach
    void setUp() {
    }

    @Test
    void test_encode() {
        List<Long> numbersToEncode = List.of(0L, 1L, 62L, 63L, 4567L, -1L, -10L);

        List<String> encodedNumbers = base62Encoder.encode(numbersToEncode);

        List<String> expected = List.of("0", "1", "10", "11", "1Bf", "-1", "-A");
        assertEquals(expected, encodedNumbers);
    }
}