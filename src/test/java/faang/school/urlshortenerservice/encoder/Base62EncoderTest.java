package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @ParameterizedTest
    @MethodSource("firstArgumentsProvider")
    public void encodeTest(List<Long> numbers, List<String> expected) {
        List<String> result = base62Encoder.encode(numbers);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @MethodSource("secondArgumentsProvider")
    public void decodeTest(String number, long expected) {
        long result = base62Encoder.decode(number);

        assertEquals(expected, result);
    }

    static Stream<Arguments> firstArgumentsProvider() {
        return Stream.of(
                Arguments.of(List.of(10L, 20L, 30L, 40L, 50L), List.of("A", "K", "U", "e", "o")),
                Arguments.of(List.of(1000L, 2000L, 3000L, 4000L, 5000L), List.of("G8", "WG", "mO", "12W", "1Ie"))
        );
    }

    static Stream<Arguments> secondArgumentsProvider() {
        return Stream.of(
                Arguments.of("K", 20),
                Arguments.of("G8", 1000),
                Arguments.of("12W", 4000),
                Arguments.of("1Ie", 5000));
    }
}