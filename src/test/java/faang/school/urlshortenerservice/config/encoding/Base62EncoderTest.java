package faang.school.urlshortenerservice.config.encoding;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;

    @Test
    void testEncode() {
        List<Long> numbers = new ArrayList<>(Arrays.asList(
                0L, 1L, 2L, 61L, 62L, 63L, 64L, 123L, 124L, 125L, 3843L, 3844L
        ));

        List<String> expected = new ArrayList<>(Arrays.asList(
                "A", "B", "C", "9", "BA", "BB", "BC", "B9", "CA", "CB", "99", "BAA"
        ));

        List<String> result = base62Encoder.encode(numbers);

        assertEquals(expected, result);
    }
}