package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    private static final int HASH_LENGTH = 6;

    private Base62Encoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new Base62Encoder(HASH_LENGTH);
    }

    @Test
    @DisplayName("Encode should return a list of strings with the same size as input")
    void encodeShouldReturnSameSizeList() {
        List<Long> input = Arrays.asList(1L, 2L, 3L, 4L, 5L);

        List<String> result = encoder.encode(input);

        assertEquals(input.size(), result.size());
    }

    @Test
    @DisplayName("Encoded strings should have length of 6")
    void encodedStringsShouldHaveLengthOf6() {
        List<Long> input = Arrays.asList(1L, 2L, 3L, 4L);

        List<String> result = encoder.encode(input);

        assertTrue(result.stream().allMatch(hash -> hash.length() == HASH_LENGTH));
    }

    @Test
    @DisplayName("Encoded strings should contain only valid Base62 characters")
    void encodedStringsShouldContainOnlyValidCharacters() {
        List<Long> input = Arrays.asList(1L, 2L, 3L, 4L);

        List<String> result = encoder.encode(input);

        assertTrue(result.stream().allMatch(hash -> hash.matches("[A-Za-z0-9]{6}")));
    }

    @Test
    @DisplayName("Encode should return unique strings for unique inputs")
    void encodeShouldReturnUniqueStringsForUniqueInputs() {
        List<Long> input = Arrays.asList(1L, 2L, 3L, 4L, 5L, 1000L, 1000000L, 1000000000L);

        List<String> result = encoder.encode(input);

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

        assertEquals(result1, result2);
    }

}