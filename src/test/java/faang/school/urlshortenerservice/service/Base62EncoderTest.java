package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.service.Base62Encoder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    @Test
    void testEncode() {
        Base62Encoder base62Encoder = new Base62Encoder();
        List<Long> numbers = Arrays.asList(1L, 22L, 2232323L);
        List<String> expected = Arrays.asList("1", "m", "dJm9");

        List<String> actual = base62Encoder.encodeNumbers(numbers);

        assertEquals(expected, actual);
    }
}
