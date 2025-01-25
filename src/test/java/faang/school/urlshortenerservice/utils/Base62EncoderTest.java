package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {
    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    void encodeSuccessTest() {
        List<Long> numbers = List.of(1L, 2L, 3L);
        assertDoesNotThrow(() -> {
            List<Hash> hashes = base62Encoder.encode(numbers);
            assertEquals(3, hashes.size());
            assertEquals("b", hashes.get(0).getHash());
            assertEquals("c", hashes.get(1).getHash());
            assertEquals("d", hashes.get(2).getHash());
        });
    }

    @Test
    void encodeEmptyListSuccessTest() {
        List<Long> numbers = Collections.emptyList();
        assertDoesNotThrow(() -> {
            List<Hash> hashes = base62Encoder.encode(numbers);
            assertEquals(0, hashes.size());
        });
    }
}
