package faang.school.urlshortenerservice.managers;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {
    private Base62Encoder base62Encoder;

    @BeforeEach
    public void setUp() {
        base62Encoder = new Base62Encoder();
    }

    @Test
    public void testEncode() {
        List<Long> numbers = Arrays.asList(1L, 62L, 123L);
        List<String> expectedResults = Arrays.asList("1", "01", "z1");

        List<String> encodedStrings = base62Encoder.encode(numbers);

        assertEquals(expectedResults, encodedStrings);
    }

    @Test
    public void testEncodeSingleNumber() {
        long number = 123L;
        String expectedResult = "z1";

        String encodedString = base62Encoder.encode(List.of(number)).get(0);

        assertEquals(expectedResult, encodedString);
    }

    @Test
    public void testEncodeZero() {
        List<Long> numbers = List.of(0L);
        List<String> expectedResults = List.of(""); // Ваш код не обрабатывает 0, нужно добавить проверку в коде

        List<String> encodedStrings = base62Encoder.encode(numbers);

        assertEquals(expectedResults, encodedStrings);
    }
}





