package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
    }

    @Test
    void encode_shouldEncodeNumbersToBase62() {
        List<Long> numbers = List.of(1L, 12345L, 999999L);
        List<String> encoded = base62Encoder.encode(numbers);

        assertNotNull(encoded);
        assertEquals(numbers.size(), encoded.size());
        encoded.forEach(encodedString -> assertFalse(encodedString.isEmpty()));
    }
}