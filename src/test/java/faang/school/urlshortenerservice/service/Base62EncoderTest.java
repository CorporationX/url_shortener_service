package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    public void encode_WhenEmptyList_ReturnEmptyList() {
        List<Long> numbers = new ArrayList<>();
        List<String> encoded = encoder.encode(numbers);
        assertEquals(0, encoded.size());
    }

    @Test
    public void encode_WhenSingleNumber_ReturnEncoding() {
        List<Long> numbers = List.of(125L);
        List<String> encoded = encoder.encode(numbers);
        assertEquals(List.of("12"), encoded);
    }

    @Test
    public void encode_WhenNumbers_ReturnListEncodings() {
        List<Long> numbers = List.of(1L, 62L, 3843L);
        List<String> encoded = encoder.encode(numbers);
        assertEquals(List.of("1", "01", "zz"), encoded);
    }
}