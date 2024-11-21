package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;

    @Test
    void testEncode() {
        List<Long> numbers = List.of(0L, 1L, 61L, 62L);

        List<String> result = base62Encoder.encode(numbers);
        assertEquals("000000", result.get(0));
        assertEquals("000001", result.get(1));
        assertEquals("00000z", result.get(2));
        assertEquals("000010", result.get(3));
    }
}