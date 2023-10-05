package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class HashCacheTest {
    @Autowired
    HashCache hashCache;

    @Test
    void t2() {
        for (int i = 0; i < 100; i++) {
            String hash = hashCache.getHash();
            Assertions.assertNotNull(hash);
        }
    }
}
