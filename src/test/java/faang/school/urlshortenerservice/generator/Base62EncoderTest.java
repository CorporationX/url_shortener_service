package faang.school.urlshortenerservice.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder encoder;

    @Test
    void testEncodeSingleNumber() {
        List<String> result = encoder.encode(List.of(0L));
        assertEquals(1, result.size());
        assertTrue(result.get(0).length() >= 6);
    }

    @Test
    void testEncodeMultipleNumbers() {
        List<Long> numbers = List.of(0L, 1L, 100L);
        List<String> result = encoder.encode(numbers);

        assertEquals(3, result.size());
        assertTrue(result.get(0).length() >= 6);
        assertTrue(result.get(1).length() >= 6);
        assertTrue(result.get(2).length() >= 6);
    }

    @Test
    void testEncodeEmptyList() {
        List<String> result = encoder.encode(List.of());
        assertTrue(result.isEmpty());
    }
}