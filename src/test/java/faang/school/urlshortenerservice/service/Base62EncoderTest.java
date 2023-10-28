package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;
    String base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    int batchSize = 2;


    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(base62Encoder, "BASE62_CHARS", base62Chars);
        ReflectionTestUtils.setField(base62Encoder, "BATCH_SIZE", batchSize);
    }

    @Test
    public void testEncode() {
        List<Long> numbers = Arrays.asList(12345L, 67890L, 9876543210L, 42L, 1000000L);
        List<String> expectedResults = Arrays.asList(
                "3D7", "Hf0", "AmOy42", "g", "4C92"
        );

        List<String> encodedResults = base62Encoder.encode(numbers);

        assertEquals(expectedResults, encodedResults);
    }

    @Test
    public void testEncodeWithEmptyInput() {
        List<Long> emptyList = Arrays.asList();
        List<String> encodedResults = base62Encoder.encode(emptyList);
        assertEquals(0, encodedResults.size());
    }

    @Test
    public void testEncodeWithSingleNumber() {
        List<Long> singleNumberList = Arrays.asList(123L);
        List<String> expectedResults = Arrays.asList("1z");

        List<String> encodedResults = base62Encoder.encode(singleNumberList);

        assertEquals(expectedResults, encodedResults);
    }

    @Test
    public void testEncodeWith100NonNegativeNumbers() {
        List<Long> numbers = generateNonNegativeRandomNumbers(10000);
        List<String> encodedResults = base62Encoder.encode(numbers);

        assertEquals(10000, encodedResults.size());
        for (String encoded : encodedResults) {
            for (char c : encoded.toCharArray()) {
                assertTrue(base62Chars.contains(String.valueOf(c)));
            }
        }
    }

    private List<Long> generateNonNegativeRandomNumbers(int count) {
        List<Long> numbers = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            long number = random.nextLong();
            if (number < 0) {
                number = -number;
            }
            numbers.add(number);
        }
        return numbers;
    }
}