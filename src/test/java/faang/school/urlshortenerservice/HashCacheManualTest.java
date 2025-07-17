package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.entity.HashCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HashCacheManualTest {

    @Autowired
    private HashCache hashCache;

    @Test
    public void testHashCache() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            String hash = hashCache.getHash();
            System.out.println("Got hash: " + hash);
        }
        Thread.sleep(2000); // Даем время для асинхронного заполнения
    }
}