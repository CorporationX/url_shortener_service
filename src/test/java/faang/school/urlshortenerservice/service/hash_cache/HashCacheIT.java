package faang.school.urlshortenerservice.service.hash_cache;

import faang.school.urlshortenerservice.UrlShortenerApplicationTests;
import faang.school.urlshortenerservice.properties.HashCacheQueueProperties;
import faang.school.urlshortenerservice.repository.hash.impl.HashRepositoryImpl;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HashCacheIT extends UrlShortenerApplicationTests {

    @Autowired
    private HashCache hashCache;

    @Autowired
    private HashCacheQueueProperties queueProp;

    @Autowired
    private HashRepositoryImpl hashRepository;

    @Autowired
    private HashGenerator hashGenerator;

    @Test
    public void fillCacheTest() {
        long localCacheBeforeTest = hashCache.getLocalHashCache().size();

        hashCache.fillCache();

        long localCacheAfterTest = hashCache.getLocalHashCache().size();

        assertTrue(localCacheBeforeTest < localCacheAfterTest);
    }

    @Test
    public void generateHashesStopsWhenThresholdIsMetTest() {
        jdbcTemplate.update("DELETE FROM hash");
        hashGenerator.generateBatchHashes(queueProp.getCountToStopGenerate() -
                queueProp.getMaxQueueSize() + getBatchSize()).join();
        hashCache.getLocalHashCache().clear();

        hashCache.fillCache().join();

        long totalHashes = hashRepository.getHashesCount();
        assertEquals(queueProp.getCountToStopGenerate(), totalHashes);
    }

    private int getBatchSize() {
        return queueProp.getMaxQueueSize() - (int) getPercentageToFill();
    }

    private double getPercentageToFill() {
        return ((double) queueProp.getMaxQueueSize() / 100) * queueProp.getPercentageToStartFill();
    }
}