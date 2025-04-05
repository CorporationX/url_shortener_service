package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class Base62EncoderTest {
    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    void testEncode_ListOfNumbers() {
        List<Long> numbers = List.of(123456789L, 987654321L);
        List<String> expected = List.of("huAWI", "ZAG0EB");
        List<String> result = base62Encoder.encode(numbers);
        Assertions.assertEquals(expected, result);
    }
}