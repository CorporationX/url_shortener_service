package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BaseEncoderTest {

    private final BaseEncoder baseEncoder = new BaseEncoder();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils
                .setField(baseEncoder,
                        "alphabet",
                        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        ReflectionTestUtils.setField(baseEncoder, "base", 62);
    }

    @Test
    void testEncode_Success() {
        List<Long> numbers = List.of(12345L, 67890L, 98765L);

        List<Hash> result = baseEncoder.encode(numbers);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("3D7", result.get(0).getHash());
        assertEquals("Hf0", result.get(1).getHash());
        assertEquals("Pgz", result.get(2).getHash());
    }

    @Test
    void testEncode_InvalidAlphabet() {
        ReflectionTestUtils.setField(baseEncoder, "alphabet", "1");

        List<Long> numbers = List.of(12345L, 67890L, 98765L);

        assertThrows(StringIndexOutOfBoundsException.class,
                () -> baseEncoder.encode(numbers));
    }

    @Test
    void testEncode_InvalidBase() {
        ReflectionTestUtils.setField(baseEncoder, "base", 0);

        List<Long> numbers = List.of(12345L, 67890L, 98765L);

        assertThrows(ArithmeticException.class,
                () -> baseEncoder.encode(numbers));
    }

    @Test
    void testEncode_EmptyList() {
        List<Long> numbers = List.of();

        List<Hash> result = baseEncoder.encode(numbers);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testEncode_InvalidNumber() {
        List<Long> numbers = List.of(-12345L, 0L, -98765L);

        assertThrows(IllegalArgumentException.class,
                () -> baseEncoder.encode(numbers));
    }
}
