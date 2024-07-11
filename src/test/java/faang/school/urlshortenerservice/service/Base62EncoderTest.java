package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {

    @Spy
    private Base62Encoder base62Encoder;

    @Test
    public void testEncode() {
        List<Long> numbers = List.of(1L, 61L);
        List<String> expected = List.of("000001", "00000z");

        List<String> result = base62Encoder.encode(numbers);

        assertEquals(expected, result);
    }
}
