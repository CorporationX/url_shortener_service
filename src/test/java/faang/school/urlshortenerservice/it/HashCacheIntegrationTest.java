package faang.school.urlshortenerservice.it;

import faang.school.urlshortenerservice.properties.HashCacheProperties;
import faang.school.urlshortenerservice.service.HashCacheService;
import faang.school.urlshortenerservice.util.ContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class HashCacheIntegrationTest extends ContainersConfig {

    @Autowired
    private HashCacheService hashCacheService;

    @Autowired
    private HashCacheProperties properties;

    @Test
    void testPositiveQueueRefill() throws InterruptedException {
        int multiplier = 3;
        List<String> allHashes = new ArrayList<>();
        for (int i = 0; i < multiplier; i++) {
            List<String> hashes = getHashesToQueue();
            allHashes.addAll(hashes);
            Thread.sleep(500);
        }

        assertEquals(allHashes.size(), properties.queueSize() * multiplier);
    }

    private List<String> getHashesToQueue() {
        List<String> hashes = new ArrayList<>();
        for (int i = 0; i < properties.queueSize(); i++) {
            hashes.add(hashCacheService.getHash());
        }
        return hashes;
    }
}
