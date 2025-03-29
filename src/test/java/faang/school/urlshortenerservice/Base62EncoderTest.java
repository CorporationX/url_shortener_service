package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.service.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
    }

    @Test
    void testEncodeLong_Zero() {
        String result = base62Encoder.encodeLong(0L);
        assertEquals("A", result);
    }

    @Test
    void testEncodeLong_SimpleNumber() {
        String result = base62Encoder.encodeLong(62L);
        assertEquals("a0", result);
    }

    @Test
    void testEncode_List() {
        List<String> result = base62Encoder.encode(List.of(1L, 62L));
        assertEquals(List.of("B", "a0"), result);
    }

    @Test
    void testEncodeLong_MaxLongValue() {
        String result = base62Encoder.encodeLong(Long.MAX_VALUE);
        assertEquals("eW4WJbRnsL", result);
    }

    @Test
    void testEncodeLong_NegativeNumber_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            base62Encoder.encodeLong(-1L);
        });
        assertEquals("Number must be non-negative", exception.getMessage());
    }

    @Test
    void testEncodeLong_SmallNumbers() {
        assertEquals("B", base62Encoder.encodeLong(1L));
        assertEquals("C", base62Encoder.encodeLong(2L));
        assertEquals("Z", base62Encoder.encodeLong(25L));
        assertEquals("a", base62Encoder.encodeLong(26L));
        assertEquals("0", base62Encoder.encodeLong(52L));
        assertEquals("9", base62Encoder.encodeLong(61L));
    }

    @Test
    void testEncode_EmptyList() {
        List<String> result = base62Encoder.encode(List.of());
        assertEquals(List.of(), result);
    }

    @Test
    void testEncode_LargeList() {
        List<Long> input = List.of(0L, 1L, 62L, 3844L);
        List<String> result = base62Encoder.encode(input);
        assertEquals(List.of("A", "B", "a0", "A00"), result);
    }

    @Test
    void testEncodeLong_PowersOf62() {
        assertEquals("a0", base62Encoder.encodeLong(62L));
        assertEquals("A00", base62Encoder.encodeLong(3844L));
        assertEquals("a000", base62Encoder.encodeLong(238328L));
    }

    @Test
    void testEncodeLong_SequentialNumbers() {
        String result1 = base62Encoder.encodeLong(12345L);
        String result2 = base62Encoder.encodeLong(12346L);
        assertEquals("DNH", result1);
        assertEquals("DNI", result2);
        assertNotEquals(result1, result2);
    }
}
