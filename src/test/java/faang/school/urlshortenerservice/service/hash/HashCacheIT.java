package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.BaseContextIT;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HashCacheIT extends BaseContextIT {
    @Autowired
    private HashCache hashCache;

    @Autowired
    private HashRepository hashRepository;

    @Test
    void hashCacheInitTest() {
        String hash = hashCache.getHash();
        assertNotNull(hash);
        assertEquals(9000, hashRepository.getHashesSize());
    }
}
