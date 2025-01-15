package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEncoderTest {
    private String characters;
    private List<Long> numbers;
    private List<String> encodedValues;
    private BaseEncoder baseEncoder;

    @BeforeEach
    void setUp() {
        characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        numbers = new ArrayList<>(List.of(1L, 2L, 3L));
        encodedValues = new ArrayList<>(List.of("1", "2", "3"));
        baseEncoder = new BaseEncoder(characters);
    }

    @Test
    void testEncodeListSuccess() {
        List<String> result = baseEncoder.encodeList(numbers);
        assertThat(result).hasSize(numbers.size());

        encodedValues.forEach(value -> assertThat(result).contains(value));
    }
}