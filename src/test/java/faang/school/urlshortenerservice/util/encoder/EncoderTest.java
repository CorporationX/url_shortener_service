package faang.school.urlshortenerservice.util.encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncoderTest {
    private Encoder<Integer, String> encoder;

    @BeforeEach
    void setUp() {
        encoder = new Encoder<>() {
            @Override
            public String encode(Integer number) {
                return number.toString();
            }
        };
    }

    @Test
    void testEncodeList() {
        List<String> correctResult = List.of("1", "2", "3");
        List<Integer> numbers = List.of(1, 2, 3);

        List<String> result = encoder.encode(numbers);

        assertEquals(correctResult, result);
    }

    @Test
    void testEncodeEmptyList() {
        List<Integer> numbers = Collections.emptyList();

        List<String> encodedList = encoder.encode(numbers);

        assertEquals(Collections.emptyList(), encodedList);
    }
}
