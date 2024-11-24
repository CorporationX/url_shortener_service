package faang.school.urlshortenerservice.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62EncoderTest {

    private BaseEncoder baseEncoder;

    @BeforeEach
    public void setUp() {
        baseEncoder = new Base62Encoder();
    }

    @Test
    public void testEncode() {
        List<Long> numbers = List.of(1L, 2L, 3L);

        List<String> hashes = baseEncoder.encode(numbers);

        assertEquals(3, hashes.size());
        assertEquals(hashes.get(0), "1");
        assertEquals(hashes.get(1), "2");
        assertEquals(hashes.get(2), "3");
    }

    @Test
    public void testEncodeWithEmptyNumbers() {
        List<Long> numbers = List.of();
        List<String> hashes = baseEncoder.encode(numbers);
        assertEquals(0, hashes.size());
    }
}
