package faang.school.urlshortenerservice.service;

import io.seruco.encoding.base62.Base62;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        encoded.forEach(encodedString -> {
            assertNotNull(encodedString);
            assertFalse(encodedString.isEmpty());
        });
        assertEquals(
                encoded.size(),
                encoded.stream().distinct().count());
    }

    @Test
    void encode_shouldReturnEmptyListForEmptyInput() {
        List<String> encoded = base62Encoder.encode(List.of());
        assertNotNull(encoded);
        assertTrue(encoded.isEmpty());
    }

    @Test
    void encode_shouldHandleSingleNumber() {
        Long number = 12345L;
        List<String> encoded = base62Encoder.encode(List.of(number));

        assertNotNull(encoded);
        assertEquals(1, encoded.size());
        assertFalse(encoded.get(0).isEmpty());

        long decodedNumber = decodeBase62(encoded.get(0));
        assertEquals(number, decodedNumber);
    }
    private long decodeBase62(String encodedString) {
        byte[] decodedBytes = Base62.createInstance().decode(encodedString.getBytes(StandardCharsets.UTF_8));
        return Long.parseLong(new String(decodedBytes, StandardCharsets.UTF_8));
    }
}