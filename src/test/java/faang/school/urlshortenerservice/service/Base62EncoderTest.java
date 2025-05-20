package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    void givenEmptyList_whenEncodeBatch_thenReturnEmptyList() {
        List<String> result = encoder.encodeBatch(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void givenListOfNumbers_whenEncodeBatch_thenReturnCorrectEncodedValues() {
        List<Long> inputs = List.of(0L, 1L, 10L, 35L, 36L, 61L, 62L);
        List<String> expected = List.of("0", "1", "A", "Z", "a", "z", "10");

        List<String> result = encoder.encodeBatch(inputs);

        assertEquals(expected, result);
    }

    @Test
    void givenUnsortedList_whenEncodeBatch_thenMaintainInputOrder() {
        List<Long> inputs = List.of(123L, 456L, 789L);
        List<String> result = encoder.encodeBatch(inputs);

        assertAll(
                () -> assertEquals("1z", result.get(0)),
                () -> assertEquals("7M", result.get(1)),
                () -> assertEquals("Cj", result.get(2))
        );
    }
}
