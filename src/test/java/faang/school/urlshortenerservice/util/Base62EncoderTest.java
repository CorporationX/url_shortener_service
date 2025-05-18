package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder base62Encoder;

    private final String testAlphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(base62Encoder, "alphabet", testAlphabet);
    }

    @Test
    void testInit_ShouldSucceed_WithValidAlphabet() {
        base62Encoder.init();

        assertTrue(true);
    }

    @Test
    void testInit_ShouldThrow_WhenAlphabetIsNull() {
        ReflectionTestUtils.setField(base62Encoder, "alphabet", null);

        assertThrows(IllegalArgumentException.class, () -> base62Encoder.init());
    }

    @Test
    void testInit_ShouldThrow_WhenAlphabetIsBlank() {
        ReflectionTestUtils.setField(base62Encoder, "alphabet", "   ");

        assertThrows(IllegalArgumentException.class, () -> base62Encoder.init());
    }

    @Test
    void testInit_ShouldThrow_WhenAlphabetLengthNot62() {
        ReflectionTestUtils.setField(base62Encoder, "alphabet", testAlphabet.substring(0, 61));

        assertThrows(IllegalArgumentException.class, () -> base62Encoder.init());
    }

    @Test
    void testInit_ShouldThrow_WhenAlphabetHasDuplicates() {
        String invalidAlphabet = testAlphabet.substring(0, 61) + "A";
        ReflectionTestUtils.setField(base62Encoder, "alphabet", invalidAlphabet);

        assertThrows(IllegalArgumentException.class, () -> base62Encoder.init());
    }

    @Test
    void testEncodeNumber_ShouldReturnCorrectEncoding() {
        base62Encoder.init();
        long[] testNumbers = {0L, 1L, 10L, 62L, 12345L, 999999L};
        String[] expected = {"0", "1", "A", "10", "3D7", "4C91"};

        for (int i = 0; i < testNumbers.length; i++) {
            String result = ReflectionTestUtils.invokeMethod(base62Encoder, "encodeNumber", testNumbers[i]);

            assertEquals(expected[i], result);
        }
    }

    @Test
    void testEncode_ShouldHandleEmptyList() {
        base62Encoder.init();

        List<String> result = base62Encoder.encode(List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void testEncode_ShouldProcessMultipleNumbers() {
        base62Encoder.init();
        List<Long> input = List.of(0L, 1L, 62L, 12345L);

        List<String> result = base62Encoder.encode(input);

        assertEquals(4, result.size());
        assertEquals("0", result.get(0));
        assertEquals("1", result.get(1));
        assertEquals("10", result.get(2));
        assertEquals("3D7", result.get(3));
    }

    @Test
    void testEncode_ShouldHandleZero() {
        base62Encoder.init();

        String result = ReflectionTestUtils.invokeMethod(base62Encoder, "encodeNumber", 0L);

        assertEquals("0", result);
    }

    @Test
    void testEncode_ShouldHandleLargeNumber() {
        base62Encoder.init();
        long largeNumber = (long) Math.pow(62, 4) - 1;

        String result = ReflectionTestUtils.invokeMethod(base62Encoder, "encodeNumber", largeNumber);

        assertEquals("zzzz", result);
    }
}