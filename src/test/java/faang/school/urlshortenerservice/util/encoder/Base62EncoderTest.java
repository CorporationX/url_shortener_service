package faang.school.urlshortenerservice.util.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Base62EncoderTest {

    private Base62Encoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new Base62Encoder();
        encoder.setHashSize(6);
        encoder.setCharacterBase62("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    @Test
    void encode_When_Success() {
        long input = 1L;
        Hash result = encoder.encode(input);

        assertEquals("000001", result.getHash());
    }

    @Test
    void encodeBatch_When_Success() {
        List<Long> batch = getBatch();
        List<Hash> expected = getEncodedBatch();
        List<Hash> result = encoder.encodeBatch(batch);

        assertEquals(expected.size(), result.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getHash(), result.get(i).getHash());
        }
    }

    @Test
    void encodeBatch_With_Empty_Batch() {
        List<Long> batch = new ArrayList<>();
        List<Hash> result = encoder.encodeBatch(batch);

        assertTrue(result.isEmpty());
    }

    private List<Long> getBatch() {
        return LongStream.range(1, 11)
                .boxed()
                .toList();
    }

    private List<Hash> getEncodedBatch() {
        return List.of(
                Hash.builder().hash("000001").build(),
                Hash.builder().hash("000002").build(),
                Hash.builder().hash("000003").build(),
                Hash.builder().hash("000004").build(),
                Hash.builder().hash("000005").build(),
                Hash.builder().hash("000006").build(),
                Hash.builder().hash("000007").build(),
                Hash.builder().hash("000008").build(),
                Hash.builder().hash("000009").build(),
                Hash.builder().hash("00000A").build()
        );
    }
}
