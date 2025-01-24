package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {
    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    void encodeSuccessTest() {
        List<Long> input = Arrays.asList(100000L, 200000L, 300000L);
        List<String> expectedResult = Arrays.asList("4aA", "Yb0", "Scqb");

        List<String> actualResult = base62Encoder.encode(input);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void encodeListNullFailTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> base62Encoder.encode(null));
        assertEquals("List must not be null or empty.", exception.getMessage());
    }

    @Test
    void encodeListEmptyFailTest() {
        List<Long> input = Collections.emptyList();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> base62Encoder.encode(input));
        assertEquals("List must not be null or empty.", exception.getMessage());
    }

    @Test
    void encodeNagativeValueFailTest() {
        List<Long> input = Arrays.asList(1L, -110L);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> base62Encoder.encode(input));
        assertEquals("List must contain only Long values.", exception.getMessage());
    }
}