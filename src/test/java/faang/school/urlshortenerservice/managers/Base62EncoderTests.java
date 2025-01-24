package faang.school.urlshortenerservice.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

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

        List<String> expectedResults = Arrays.asList("b", "ab", "9b");

        List<String> encodedStrings = base62Encoder.encode(numbers);

        assertEquals(expectedResults, encodedStrings);
    }

    @Test
    public void testApplyBase62Encoding() {
        long number = 123L;
        String expectedResult = "9b";

        String encodedString = base62Encoder.encode(Arrays.asList(number)).get(0);

        assertEquals(expectedResult, encodedString);
    }
}
