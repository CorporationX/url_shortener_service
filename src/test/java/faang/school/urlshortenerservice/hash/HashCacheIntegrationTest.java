package faang.school.urlshortenerservice.hash;


import faang.school.urlshortenerservice.properties.HashProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//public class HashCacheIntegrationTest {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private HashProperties hashProperties;
//
//    @Autowired
//    private HashCache hashCache;
//
//    @Autowired
//    private ThreadPoolTaskExecutor threadPool;
//
//
//    @Test
//    public void testFreeHashSetState() {
//        String sql = "SELECT COUNT(*) FROM free_hash_set";
//        Long resultCount = jdbcTemplate.queryForObject(sql, Long.class);
//
//        Long expectedCount = (long) hashProperties.getGenerateSize() - hashProperties.getHashBatchSize();
//        assertEquals(expectedCount, resultCount);
//    }
//
//    @Test
//    public void testGenerateHash_ExactlyOnce() throws InterruptedException {
//        int count = (int) (hashProperties.getHashBatchSize() - hashProperties.getCacheCapacity() * hashProperties.getLowThreshold());
//        for (int i = 0; i < count; i++) {
//            hashCache.getHash();
//        }
//
//        Runnable hashGetter = () -> hashCache.getHash();
//        threadPool.execute(hashGetter);
//        threadPool.execute(hashGetter);
//
//        SECONDS.sleep(1);
//
//        Long expectedCount = 2L * hashProperties.getGenerateSize() - 2L * hashProperties.getHashBatchSize();
//
//        String sql = "SELECT COUNT(*) FROM free_hash_set";
//        Long resultCount = jdbcTemplate.queryForObject(sql, Long.class);
//        assertEquals(expectedCount, resultCount);
//    }
//}
