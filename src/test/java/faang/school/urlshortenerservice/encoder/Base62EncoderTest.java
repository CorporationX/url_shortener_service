package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
        ReflectionTestUtils.setField(base62Encoder, "base62Characters",
                "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    @Test
    void shouldEncodeEmptyList() {
        List<Long> input = Arrays.asList();
        List<String> result = base62Encoder.encode(input);
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void shouldEncode(List<Long> input, List<String> expected) {
        List<String> result = base62Encoder.encode(input);
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(0L),
                        Arrays.asList("0")
                ),
                Arguments.of(
                        Arrays.asList(1L, 62L, 3844L),
                        Arrays.asList("1", "10", "100")
                ),
                Arguments.of(
                        Arrays.asList(61L, 125L, 999999L),
                        Arrays.asList("z", "21", "4C91")
                )
        );
    }

    @Test
    void shouldEncodeNegativeNumber() {
        List<Long> input = Arrays.asList(-1L);
        List<String> result = base62Encoder.encode(input);
        assertEquals(Arrays.asList(""), result);
    }

    @Test
    void shouldEncodeLargeNumber() {
        List<Long> input = Arrays.asList(9007199254740991L);
        List<String> result = base62Encoder.encode(input);
        assertEquals(Arrays.asList("fFgnDxSe7"), result);
    }
}