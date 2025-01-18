package faang.school.urlshortenerservice.base62;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(base62Encoder, "alphabetBase62", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    @DisplayName("Test encode method")
    @Test
    void testEncode() {
        List<Long> inputNumbers = Arrays.asList(1L, 2L, 62L, 63L, 3844L, 238328L);

        List<Hash> expectedHashes = Arrays.asList(
                new Hash("1"),
                new Hash("2"),
                new Hash("10"),
                new Hash("11"),
                new Hash("100"),
                new Hash("1000")
        );

        List<Hash> actualHashes = base62Encoder.encode(inputNumbers);

        assertEquals(expectedHashes.size(), actualHashes.size());

        for (int i = 0; i < expectedHashes.size(); i++) {
            assertEquals(expectedHashes.get(i).getHash(), actualHashes.get(i).getHash());
        }
    }

    @DisplayName("Test encode empty list")
    @Test
    void testEncodeEmptyList() {
        List<Long> inputNumbers = List.of();

        List<Hash> actualHashes = base62Encoder.encode(inputNumbers);

        assertEquals(0, actualHashes.size());
        assertTrue(actualHashes.isEmpty(), "Result should be empty");
    }

    @DisplayName("Test encode with single large number")
    @Test
    void testEncodeSingleLargeNumber() {
        List<Long> inputNumbers = List.of(123456789L);

        List<Hash> expectedHashes = List.of(new Hash("8M0kX"));

        List<Hash> actualHashes = base62Encoder.encode(inputNumbers);

        assertEquals(expectedHashes.size(), actualHashes.size());
        assertEquals(expectedHashes.get(0).getHash(), actualHashes.get(0).getHash());
    }
}
