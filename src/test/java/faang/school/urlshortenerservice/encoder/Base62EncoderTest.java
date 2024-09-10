package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class Base62EncoderTest {

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @ParameterizedTest
    @CsvSource({
            "'1,2,3', '0,0,0'",
            "'10,20,30', '0,0,0'",
            "'5,15,25,35', '0,0,0'"
    })
    void testEncodeNegativeCases(String numbersParam, String resultParam) {
        List<Long> numbers = Arrays.stream(numbersParam.split(","))
                .map(Long::parseLong)
                .toList();
        List<String> unexpectedResult = Arrays.asList(resultParam.split(","));

        List<String> actualResult = base62Encoder.encode(numbers);
        assertNotEquals(unexpectedResult, actualResult);
    }

    @ParameterizedTest
    @CsvSource({
            "'1,61,62', '1,z,10'",
            "'123456,987654321,123456789012345', 'W7E,14q60P,Z3WbxDVB'",
    })
    void testEncodePositiveCases(String numbersParam, String resultParam) {
        List<Long> numbers = Arrays.stream(numbersParam.split(","))
                .map(Long::parseLong)
                .toList();
        List<String> expectedResult = Arrays.asList(resultParam.split(","));

        List<String> actualResult = base62Encoder.encode(numbers);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testEncodeShouldBeUnique() {
        List<Long> numbers = List.of(1L, 1L, 1L);

        Set<String> resultAsSet = new HashSet<>(base62Encoder.encode(numbers));

        assertThat(resultAsSet).hasSize(1);

    }
}