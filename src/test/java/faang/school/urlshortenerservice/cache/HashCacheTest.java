package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @InjectMocks
    private HashCache hashCache;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "capacity", 5);
        ReflectionTestUtils.setField(hashCache, "minSize", 0.2);
        ReflectionTestUtils.setField(hashCache, "amountFill", 0.8);
    }

    @Test
    void testGetHash_notFillHashes() {
        Hash hash1 = new Hash("1");
        Hash hash2 = new Hash("2");

        Queue<Hash> hashes = new ArrayBlockingQueue<>(5);
        hashes.add(hash1);
        hashes.add(hash2);
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);

        Hash actual = hashCache.getHash();

        assertEquals(hash1, actual);
    }

    @Test
    void testGetHash_fillHashes() {
        Hash hash1 = new Hash("1");

        Queue<Hash> hashes = new ArrayBlockingQueue<>(5);
        hashes.add(hash1);
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);

        Hash actual = hashCache.getHash();

        assertEquals(hash1, actual);
        Mockito.verify(hashRepository).getHashBatch(4);
    }
}