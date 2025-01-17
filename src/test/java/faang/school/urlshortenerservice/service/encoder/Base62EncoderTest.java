package faang.school.urlshortenerservice.service.encoder;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    void testEncodeSingleNumber() {
        List<Long> input = List.of(12345L);
        List<String> expectedOutput = List.of("3D7");

        List<String> actualOutput = encoder.encode(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void testEncodeMultipleNumbers() {
        List<Long> input = List.of(1L, 62L, 12345L, 987654321L);
        List<String> expectedOutput = List.of("1", "10", "3D7", "14q60P");

        List<String> actualOutput = encoder.encode(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void testEncodeEmptyList() {
        List<Long> input = List.of();
        List<String> expectedOutput = List.of();

        List<String> actualOutput = encoder.encode(input);

        assertEquals(expectedOutput, actualOutput);
    }
}