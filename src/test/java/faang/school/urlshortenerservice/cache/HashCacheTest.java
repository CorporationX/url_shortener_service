package faang.school.urlshortenerservice.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ArrayBlockingQueue;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Queue;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {
    @InjectMocks
    private HashCache hashCache;
    @Mock
    private HashGenerator hashGenerator;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(hashCache, "capacity", 10);
        ReflectionTestUtils.setField(hashCache, "isFlag", new AtomicBoolean(false));
    }

    @Test
    void testGetHashSuccess() {
        Queue<Hash> hashes = new ArrayBlockingQueue<>(10);
        hashes.add(new Hash("a000001"));
        hashes.add(new Hash("b000001"));
        hashes.add(new Hash("c000001"));
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);

        hashCache.getHash();
        assertEquals(2, hashes.size());
    }
}
