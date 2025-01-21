package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder base62Encoder;

    @ParameterizedTest
    @MethodSource("provideNumbersForEncodeTest")
    void encodeListTest(List<Long> inputs, List<String> expected) {
        String[] result = base62Encoder.encode(inputs);
        assertArrayEquals(expected.toArray(new String[0]), result);
        for (String link : result) {
            assertTrue(link.length() >= 6);
        }
    }

    static Stream<Arguments> provideNumbersForEncodeTest() {
        return Stream.of(
                Arguments.of(List.of(916132832L, 926132832L), List.of("aaaaab", "uC7Pab")),
                Arguments.of(List.of(916932832L, 916132833L), List.of("ohwdab", "baaaab"))
        );
    }
}
