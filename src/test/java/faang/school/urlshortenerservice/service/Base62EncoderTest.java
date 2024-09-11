package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    private Base62Encoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new Base62Encoder();
    }

    @Test
    @DisplayName("Encode should return a list of strings with the same size as input")
    void encodeShouldReturnSameSizeList() {
        List<Long> input = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        List<String> result = encoder.encode(input);
        System.out.println(result);
        assertEquals(input.size(), result.size());
    }

    @Test
    @DisplayName("Encoded strings should have length of 6")
    void encodedStringsShouldHaveLengthOf6() {
        List<Long> input = Arrays.asList(1L, 2L, 3L, 4L);
        List<String> result = encoder.encode(input);
        System.out.println(result);
        for (String encoded : result) {
            assertEquals(6, encoded.length());
        }
    }

    @Test
    @DisplayName("Encoded strings should contain only valid Base62 characters")
    void encodedStringsShouldContainOnlyValidCharacters() {
        List<Long> input = Arrays.asList(1L, 2L, 3L, 4L);
        List<String> result = encoder.encode(input);
        System.out.println(result);
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (String encoded : result) {
            for (char c : encoded.toCharArray()) {
                assertNotEquals(-1, validChars.indexOf(c));
            }
        }
    }

    @Test
    @DisplayName("Encode should return unique strings for unique inputs")
    void encodeShouldReturnUniqueStringsForUniqueInputs() {
        List<Long> input = Arrays.asList(1L, 2L, 3L, 4L, 5L, 1000L, 1000000L, 1000000000L);
        List<String> result = encoder.encode(input);
        System.out.println(result);
        Set<String> uniqueResults = new HashSet<>(result);
        assertEquals(input.size(), uniqueResults.size());
    }

    @Test
    @DisplayName("Encode should return same output for same input")
    void encodeShouldReturnSameOutputForSameInput() {
        List<Long> input1 = Arrays.asList(1L, 1000L, 1000000L);
        List<Long> input2 = Arrays.asList(1L, 1000L, 1000000L);
        List<String> result1 = encoder.encode(input1);
        List<String> result2 = encoder.encode(input2);
        System.out.println(result1);
        System.out.println(result2);
        assertEquals(result1, result2);
    }

}