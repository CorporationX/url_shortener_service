package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Base62EncoderTest {

    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
    }

    private static Stream<Object[]> base62EncodingProvider() {
        return Stream.of(
                new Object[]{List.of(12345L), List.of("3D7")},
                new Object[]{List.of(12345L, 67890L, 0L), List.of("3D7", "Hf0", "0")},
                new Object[]{List.of(Long.MAX_VALUE), List.of("AzL8n0Y58m7")}
        );
    }

    @ParameterizedTest
    @MethodSource("base62EncodingProvider")
    void encodeNumbers_returnsBase62EncodedStrings(List<Long> numbers, List<String> expected) {
        assertEquals(expected, base62Encoder.encode(numbers));
    }

    @Test
    void encodeNegativeNumber_throwsIllegalArgumentException() {
        List<Long> numbers = List.of(-1L);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> base62Encoder.encode(numbers));
        assertEquals("Number must be greater than or equal to 0", e.getMessage());
    }
}