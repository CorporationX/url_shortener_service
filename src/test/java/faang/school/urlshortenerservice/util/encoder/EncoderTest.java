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
                return "Encoded_" + number;
            }
        };
    }

    @Test
    public void testEncodeList() {
        List<String> correctResult = List.of("Encoded_123", "Encoded_456", "Encoded_789");
        List<Integer> numbers = List.of(123, 456, 789);

        List<String> result = encoder.encode(numbers);

        assertEquals(correctResult, result);
    }

    @Test
    public void testEncodeEmptyList() {
        List<Integer> numbers = Collections.emptyList();

        List<String> encodedList = encoder.encode(numbers);

        assertEquals(Collections.emptyList(), encodedList);
    }
}