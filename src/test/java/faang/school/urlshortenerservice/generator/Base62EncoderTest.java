package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    private final Base62Encoder encoder = new Base62Encoder();

    @ParameterizedTest
    @MethodSource("provideNumbersAndExpectedHashes")
    void encode_ShouldReturnCorrectHashes(long number, String expectedHash) {
        List<Long> input = List.of(number);

        List<Hash> result = encoder.encode(input);

        assertEquals(1, result.size());
        assertEquals(expectedHash, result.get(0).getHash());
    }

    private static Stream<Arguments> provideNumbersAndExpectedHashes() {
        return Stream.of(
                Arguments.of(0L, ""),
                Arguments.of(1L, "1"),
                Arguments.of(10L, "A"),
                Arguments.of(35L, "Z"),
                Arguments.of(36L, "a"),
                Arguments.of(61L, "z"),
                Arguments.of(62L, "10"),
                Arguments.of(123L, "1z")
        );
    }
}