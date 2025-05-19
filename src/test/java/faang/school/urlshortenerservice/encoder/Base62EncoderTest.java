package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    private Base62Encoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new Base62Encoder();
    }

    @Test
    void testEncodeSingleNumber() {
        List<Long> numbers = List.of(125L);

        List<String> result = encoder.encode(numbers);

        assertEquals(1, result.size());
        assertEquals("CB", result.get(0));
    }

    @Test
    void testEncodeMultipleNumbers() {
        List<Long> numbers = List.of(0L, 1L, 10L, 62L, 3844L);

        List<String> result = encoder.encode(numbers);

        assertEquals(List.of("A", "B", "K", "BA", "BAA"), result);
    }
}
