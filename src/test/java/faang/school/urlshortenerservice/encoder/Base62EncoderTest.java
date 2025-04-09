package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Base62EncoderTest {
    Base62Encoder base62Encoder = new Base62Encoder();
    List<Long> numbers;
    List<String> expectedHashes = new ArrayList<>(Arrays.asList("0","z", "01"));

    @BeforeEach
    void setUp() {
        numbers = new ArrayList<>(Arrays.asList(0L, 61L, 62L));
    }

    @Test
    void testEncode() {
        List<String> hashes = base62Encoder.encode(numbers);
        Assertions.assertEquals(expectedHashes, hashes, "Arrays are not equal");
    }
}
