package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static utils.TestData.generateHashes;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {
    @Mock
    private HashGenerator hashGenerator;
    @InjectMocks
    private HashCache hashCache;

    private List<String> hashes;
    private Field capacity;

    @BeforeEach
    void setUp() throws Exception {
        hashes = generateHashes(10, 6);
        capacity = HashCache.class.getDeclaredField("capacity");
        capacity.setAccessible(true);
        capacity.set(hashCache, hashes.size());

        Field minPercentHashes = HashCache.class.getDeclaredField("minPercentHashes");
        minPercentHashes.setAccessible(true);
        minPercentHashes.set(hashCache, 20);

        when(hashGenerator.getHashes(hashes.size())).thenReturn(hashes);
        hashCache.init();
    }

    @Test
    public void testGetHash() {
        String result = hashCache.getHash();

        String firstHash = hashes.stream().findFirst().get();
        assertEquals(result, firstHash);
    }

    @Test
    public void testGetHashLowHashes() throws Exception {
        capacity.set(hashCache, 100);

        when(hashGenerator.getHashes(100)).thenReturn(hashes);
        String result = hashCache.getHash();

        Thread.sleep(100);
        String firstHash = hashes.stream().findFirst().get();
        assertEquals(result, firstHash);
    }
}
