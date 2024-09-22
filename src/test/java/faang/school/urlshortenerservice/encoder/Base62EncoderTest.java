package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Base62EncoderTest {
    private Base62Encoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new Base62Encoder();
    }

    @Test
    void encoder_ShouldReturnCorrectBase62Hash_ForGivenNumbers() {
        List<Long> numbers = List.of(1L, 10L, 61L, 62L, 12345L);
        List<String> hashes = encoder.encoder(numbers);

        assertEquals(5, hashes.size());
        assertEquals("1", hashes.get(0));
        assertEquals("a", hashes.get(1));
        assertEquals("Z", hashes.get(2));
        assertEquals("10", hashes.get(3));
        assertEquals("3d7", hashes.get(4));
    }

    @Test
    void encoder_ShouldReturnEmptyList_WhenGivenEmptyInput() {
        List<Long> emptyList = List.of();
        List<String> hashes = encoder.encoder(emptyList);

        assertTrue(hashes.isEmpty());
    }

    @Test
    void encoder_ShouldHandleLargeNumbersCorrectly() {
        List<Long> numbers = List.of(9876543210L);
        List<String> hashes = encoder.encoder(numbers);

        assertEquals(1, hashes.size());
        assertEquals("aMoY42", hashes.get(0));
    }

    @Test
    void encoder_ShouldHandleSingleNumberCorrectly() {
        List<Long> numbers = List.of(100L);
        List<String> hashes = encoder.encoder(numbers);

        assertEquals(1, hashes.size());
        assertEquals("1C", hashes.get(0));
    }
}
