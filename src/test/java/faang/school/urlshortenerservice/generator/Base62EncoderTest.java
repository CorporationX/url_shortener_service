package faang.school.urlshortenerservice.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @ParameterizedTest(name = "encodeToBase62({0}) should return \"{1}\"")
    @CsvSource({
            "1, 1",
            "10, A",
            "61, z",
            "62, 10",
            "123, 1z",
            "124, 20",
            "3843, zz",
            "238328, 1000",
            "999999999, 15ftgF"
    })
    @DisplayName("Test Base62 encoding for various numbers")
    void testEncodeToBase62(long input, String expected) {
        assertEquals(expected, base62Encoder.encodeToBase62(input));
    }
}
