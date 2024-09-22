package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@Disabled
public class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder base62Encoder;

    @Value("${base62.chars}")
    private String base62Chars;

    @Test
    @DisplayName("Encode a single number correctly")
    public void testEncodeSingleNumber() {
        Long number = 12345L;
        String expectedEncoded = "dnh";  // Ожидаемый результат для числа 12345

        List<String> encodedResult = base62Encoder.encode(List.of(number));
        assertEquals(1, encodedResult.size());
        assertEquals(expectedEncoded, encodedResult.get(0));
    }

    @Test
    @DisplayName("Encode multiple numbers correctly")
    public void testEncodeMultipleNumbers() {
        List<Long> numbers = List.of(1L, 62L, 123L, 999L);
        List<String> expectedEncoded = List.of("1", "10", "1z", "g7");  // Ожидаемые Base62 значения

        List<String> encodedResult = base62Encoder.encode(numbers);
        assertEquals(expectedEncoded.size(), encodedResult.size());
        assertEquals(expectedEncoded, encodedResult);
    }

    @Test
    @DisplayName("Encode a large number correctly")
    public void testEncodeLargeNumber() {
        Long largeNumber = 123456789L;
        String expectedEncoded = "8m0Kx";  // Ожидаемый результат для числа 123456789

        List<String> encodedResult = base62Encoder.encode(List.of(largeNumber));
        assertEquals(1, encodedResult.size());
        assertEquals(expectedEncoded, encodedResult.get(0));
    }

    @Test
    @DisplayName("Encode an empty list returns an empty result")
    public void testEmptyList() {
        List<String> encodedResult = base62Encoder.encode(List.of());
        assertEquals(0, encodedResult.size());
    }

    @Test
    @DisplayName("Encode zero correctly")
    public void testEncodeZero() {
        Long zero = 0L;
        String expectedEncoded = "0";  // Ожидаемый результат для числа 0

        List<String> encodedResult = base62Encoder.encode(List.of(zero));
        assertEquals(1, encodedResult.size());
        assertEquals(expectedEncoded, encodedResult.get(0));
    }
}