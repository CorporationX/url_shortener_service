package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    @Test
    void testEncodeListOfNumbers() {
        Base62Encoder base62Encoder = new Base62Encoder();
        List<Long> numbers = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        List<String> encodedNumbers = base62Encoder.encodeListOfNumbers(numbers);
        assertEquals(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "A"), encodedNumbers);
    }
}