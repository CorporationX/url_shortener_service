package faang.school.urlshortenerservice.generator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    private final List<Long> input = List.of(1L, 35L, 36L, 62L, 3843L);
    private final List<String> expected = List.of("1", "Z", "a", "10", "zz");

    @Test
    void encode_shouldConvertLongsToBase62Strings() {
        List<String> result = encoder.encode(input);
        assertEquals(expected, result);
    }
}