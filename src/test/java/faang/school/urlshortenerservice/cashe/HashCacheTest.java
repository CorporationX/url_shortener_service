package faang.school.urlshortenerservice.cashe;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.properties.CacheProperties;
import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import faang.school.urlshortenerservice.service.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    private HashCache hashCache;

    private final List<String> testHashes = List.of("a", "b");
    private final String testHash = "ab";

    @BeforeEach
    void setUp() {
        hashCache = new HashCache();
    }

    @Test
    void poll_shouldReturnAddedHash() {
        hashCache.addAll(List.of(testHash));
        String hash = hashCache.poll();
        assertEquals(testHash, hash);
    }

    @Test
    void addAll_shouldAddToCache() {
        hashCache.addAll(testHashes);
        assertEquals(testHashes.size(), hashCache.size());
    }
}
