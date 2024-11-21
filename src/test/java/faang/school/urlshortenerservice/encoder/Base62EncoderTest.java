package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62EncoderTest {
    private final Base62Encoder base62Encoder = new Base62Encoder();

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(base62Encoder, "hashSize", 6);
    }

    @Test
    public void testEncode() {
        List<Long> numbers = List.of(1L, 2L, 3L);

        List<String> result = base62Encoder.encode(numbers);

        assertEquals(3, result.size());
        assertEquals(result.get(0), "100000");
        assertEquals(result.get(1), "200000");
        assertEquals(result.get(2), "300000");
    }

    @Test
    public void testEncodeEmptyList() {
        List<Long> numbers = List.of();
        List<String> result = base62Encoder.encode(numbers);
        assertEquals(0, result.size());
    }
}
