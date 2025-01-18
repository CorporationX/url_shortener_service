package faang.school.urlshortenerservice.managers;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Base62EncoderTests {

    private Base62Encoder base62Encoder;

    @BeforeEach
    public void setUp() {
        base62Encoder = new Base62Encoder();
    }

    @Test
    public void testEncode() {
        List<Long> numbers = Arrays.asList(1L, 62L, 123L);

        List<String> expectedResults = Arrays.asList("b", "B", "21");

        List<String> encodedStrings = base62Encoder.encode(numbers);

        assertEquals(expectedResults, encodedStrings);
    }

    @Test
    public void testApplyBase62Encoding() {
        long number = 123L;
        String expectedResult = "21";

        String encodedString = base62Encoder.encode(Arrays.asList(number)).get(0);

        assertEquals(expectedResult, encodedString);
    }
}
