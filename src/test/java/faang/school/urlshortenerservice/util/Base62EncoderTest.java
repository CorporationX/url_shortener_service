package faang.school.urlshortenerservice.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class Base62EncoderTest {

    public static final Base62Encoder BASE_62_ENCODER = new Base62Encoder();

    @ParameterizedTest
    @MethodSource("encodeParameters")
    void encode(long input, String expectedOutput) {
        assertThat(BASE_62_ENCODER.encode(input)).isEqualTo(expectedOutput);
    }

    static Stream<Arguments> encodeParameters() {
        return Stream.of(
                Arguments.of(0L, ""),
                Arguments.of(1L, "B"),
                Arguments.of(61L, "9"),
                Arguments.of(62L, "AB"),
                Arguments.of(12345L, "HND")
        );
    }
}