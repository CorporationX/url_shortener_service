package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.model.Hash;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    void encode_shouldReturnEmptyList_whenInputIsEmpty() {
        List<Long> input = List.of();
        List<Hash> result = encoder.encode(input);

        assertTrue(result.isEmpty());
    }

    @Test
    void encode_shouldThrowException_whenInputIsNull() {
        assertThrows(NullPointerException.class,
                () -> encoder.encode(null));
    }

    @Test
    void encode_shouldProcessFullList() {
        List<Long> input = List.of(1L, 10L, 62L, 1000L);
        List<Hash> result = encoder.encode(input);

        assertEquals(4, result.size());
        assertEquals("1", result.get(0).getHash());
        assertEquals("A", result.get(1).getHash());
        assertEquals("01", result.get(2).getHash());
        assertEquals("8G", result.get(3).getHash());
    }

    @ParameterizedTest
    @MethodSource("provideNumbersForEncoding")
    void encode_shouldReturnCorrectHashes(long input, String expected) {
        List<Hash> result = encoder.encode(List.of(input));
        assertEquals(1, result.size());
        assertEquals(expected, result.get(0).getHash());
    }

    @Test
    void encode_shouldHandleZero() {
        List<Hash> result = encoder.encode(List.of(0L));
        assertEquals(1, result.size());
        assertEquals("", result.get(0).getHash());
    }

    private static Stream<Arguments> provideNumbersForEncoding() {
        return Stream.of(
                Arguments.of(1L, "1"),
                Arguments.of(10L, "A"),
                Arguments.of(35L, "Z"),
                Arguments.of(36L, "a"),
                Arguments.of(61L, "z"),
                Arguments.of(62L, "01"),
                Arguments.of(3844L, "001"),
                Arguments.of(238328L, "0001"),
                Arguments.of(1087388483L, "vPZaB1")
        );
    }
}