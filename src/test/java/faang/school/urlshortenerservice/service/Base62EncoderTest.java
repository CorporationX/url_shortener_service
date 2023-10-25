package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    @Spy
    private Base62Encoder base62Encoder;

    @ParameterizedTest
    @MethodSource("generateTestData")
    void encodeSequence(List<Long> input, List<String> expected) {
        List<String> result = base62Encoder.encodeSequence(input);
        assertEquals(expected, result);
    }

    private static Stream<Arguments> generateTestData() {
        return Stream.of(
                arguments(List.of(10L, 11L, 12L), List.of("A", "B", "C")),
                arguments(List.of(1L, 2L, 3L), List.of("1", "2", "3"))
        );
    }
}