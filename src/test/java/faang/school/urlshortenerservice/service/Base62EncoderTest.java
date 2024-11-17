package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    @InjectMocks
    Base62Encoder base62Encoder;

    long number1 = 1_234_567_890L;
    long number2 = 1_111_111_111L;
    long number3 = 2_222_222_222L;

    String hash1 = "1LY7VK";
    String hash2 = "1DC6kx";
    String hash3 = "2QODVu";

    List<Long> numbers = List.of(number1, number2, number3);
    List<String> hashes = List.of(hash1, hash2, hash3);

    @Test
    void testEncode() {
        List<String> encoded = base62Encoder.encodeList(numbers);
        assertTrue(encoded.containsAll(hashes));
        assertTrue(hashes.containsAll(encoded));
    }

    @Test
    void testNumberAsEncodedString() {
        String encoded = base62Encoder.encode(number1);
        assertEquals(hash1, encoded);
    }
}