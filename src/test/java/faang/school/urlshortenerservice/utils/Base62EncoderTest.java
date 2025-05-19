package faang.school.urlshortenerservice.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder base62Encoder;

    @Test
    void encodeBatch_ShouldReturnCorrectEncodedValueForSingleInput() {
        List<Long> input = List.of(0L);
        assertEquals("0", base62Encoder.encodeBatch(input).get(0));

        input = List.of(10L);
        assertEquals("A", base62Encoder.encodeBatch(input).get(0));

        input = List.of(35L);
        assertEquals("Z", base62Encoder.encodeBatch(input).get(0));

        input = List.of(36L);
        assertEquals("a", base62Encoder.encodeBatch(input).get(0));

        input = List.of(61L);
        assertEquals("z", base62Encoder.encodeBatch(input).get(0));

        input = List.of(62L);
        assertEquals("10", base62Encoder.encodeBatch(input).get(0));
    }

    @Test
    void encodeBatch_ShouldHandleNullInput() {
        List<String> result = base62Encoder.encodeBatch(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void encodeBatch_ShouldHandleEmptyList() {
        List<String> result = base62Encoder.encodeBatch(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void encodeBatch_ShouldEncodeAllElements() {
        List<Long> input = List.of(0L, 10L, 35L, 36L, 61L, 62L);
        List<String> expected = List.of("0", "A", "Z", "a", "z", "10");

        List<String> result = base62Encoder.encodeBatch(input);
        assertIterableEquals(expected, result);
    }
}