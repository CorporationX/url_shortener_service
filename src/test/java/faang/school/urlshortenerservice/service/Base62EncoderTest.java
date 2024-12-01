package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    private Base62Encoder base62Encoder;
    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
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
        assertTrue(encoded.stream().noneMatch(String::isEmpty));
        assertEquals(
                encoded.size(),
                encoded.stream().distinct().count());

        List<Long> decodedNumbers = encoded.stream()
                .map(this::decodeBase62)
                .toList();
        assertEquals(numbers, decodedNumbers);
    }

    @Test
    void encode_shouldReturnEmptyListForEmptyInput() {
        List<Long> numbers = List.of();
        List<String> encoded = base62Encoder.encode(numbers);
        assertNotNull(encoded);
        assertTrue(encoded.isEmpty());
    }

    @Test
    void encode_shouldHandleSingleNumber() {
        Long number = 12345L;
        List<String> encoded = base62Encoder.encode(List.of(number));

        assertNotNull(encoded);
        assertEquals(1, encoded.size());
        String encodedString = encoded.get(0);
        assertFalse(encodedString.isEmpty());

        long decodedNumber = decodeBase62(encodedString);
        assertEquals(number, decodedNumber);
    }

    private long decodeBase62(String encodedString) {
        long result = 0;
        for (char c : encodedString.toCharArray()) {
            int index = BASE62_CHARACTERS.indexOf(c);
            if (index == -1) {
                throw new IllegalArgumentException("Invalid character in encoded string: " + c);
            }
            result = result * BASE62_CHARACTERS.length() + index;
        }
        return result;
    }
}