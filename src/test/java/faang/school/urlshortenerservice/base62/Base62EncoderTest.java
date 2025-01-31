package faang.school.urlshortenerservice.base62;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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
    void testEncodeUniqueness() {
        int numberOfInputs = 100_000;

        List<Long> inputNumbers = LongStream.rangeClosed(1, numberOfInputs)
                .boxed()
                .collect(Collectors.toList());

        List<Hash> actualHashes = base62Encoder.encode(inputNumbers);

        Set<String> uniqueHashes = actualHashes.stream()
                .map(Hash::getHash)
                .collect(Collectors.toSet());

        assertEquals(numberOfInputs, uniqueHashes.size(),
                "Generated hashes are not unique");
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
