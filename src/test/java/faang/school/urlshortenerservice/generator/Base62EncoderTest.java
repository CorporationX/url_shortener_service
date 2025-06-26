package faang.school.urlshortenerservice.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    private Base62Encoder base62Encoder;
    private static final String TEST_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int TEST_HASH_LENGTH = 6;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
        ReflectionTestUtils.setField(base62Encoder, "BASE62", TEST_ALPHABET);
        ReflectionTestUtils.setField(base62Encoder, "HASH_LENGTH", TEST_HASH_LENGTH);
        base62Encoder.init();
    }

    @Test
    void testEmptyList() {
        List<Long> emptyList = List.of();
        List<String> result = base62Encoder.encode(emptyList);
        assertTrue(result.isEmpty());
    }

    @Test
    void testNullList() {
        assertThrows(NullPointerException.class, () -> base62Encoder.encode(null));
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void testNumberEncoding(List<Long> input, List<String> expected) {
        List<String> result = base62Encoder.encode(input);
        assertEquals(expected, result);
    }

    @Test
    void testNegativeNumber() {
        List<Long> numbers = List.of(-1L);
        assertThrows(IllegalArgumentException.class, () -> base62Encoder.encode(numbers));
    }

    @Test
    void testNumberExceedingMaxValue() {
        long maxValue = (long) Math.pow(TEST_ALPHABET.length(), TEST_HASH_LENGTH) - 1;
        List<Long> numbers = List.of(maxValue + 1);
        assertThrows(IllegalArgumentException.class, () -> base62Encoder.encode(numbers));
    }

    @Test
    void testEncodedStringLength() {
        List<Long> numbers = List.of(1L, 10L, 100L, 1000L);
        List<String> result = base62Encoder.encode(numbers);
        
        for (String encoded : result) {
            assertEquals(TEST_HASH_LENGTH, encoded.length(), 
                "Encoded string length should be " + TEST_HASH_LENGTH);
        }
    }

    @Test
    void testEncodedStringCharacters() {
        List<Long> numbers = List.of(1L, 10L, 100L, 1000L);
        List<String> result = base62Encoder.encode(numbers);
        
        Set<Character> alphabetSet = TEST_ALPHABET.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toSet());
        
        for (String encoded : result) {
            for (char c : encoded.toCharArray()) {
                assertTrue(alphabetSet.contains(c), 
                    "Encoded string should only contain characters from the alphabet");
            }
        }
    }

    @Test
    void testUniqueEncoding() {
        List<Long> numbers = List.of(1L, 2L, 3L, 4L, 5L);
        List<String> result = base62Encoder.encode(numbers);
        
        Set<String> uniqueResults = new java.util.HashSet<>(result);
        assertEquals(numbers.size(), uniqueResults.size(), 
            "Each number should have a unique encoding");
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
            // Test case 1: Zero
            Arguments.of(
                List.of(0L),
                List.of("000000")
            ),
            // Test case 2: Small numbers
            Arguments.of(
                List.of(1L, 2L, 3L),
                List.of("000001", "000002", "000003")
            ),
            // Test case 3: Numbers requiring multiple digits
            Arguments.of(
                List.of(62L, 63L, 64L),
                List.of("000010", "000011", "000012")
            ),
            // Test case 4: Maximum value
            Arguments.of(
                List.of((long) Math.pow(TEST_ALPHABET.length(), TEST_HASH_LENGTH) - 1),
                List.of("zzzzzz")
            ),
            // Test case 5: Mixed numbers
            Arguments.of(
                List.of(0L, 1L, 62L, 63L),
                List.of("000000", "000001", "000010", "000011")
            )
        );
    }
} 