package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.cleaner.url.CleanerScheduler;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.util.BaseContextTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableAsync
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HashCacheIntegrationTest extends BaseContextTest {

    @Autowired
    private HashCache hashCache;
    @Autowired
    private HashRepository hashRepository;
    @MockBean
    private CleanerScheduler cleanerScheduler;

    @Test
    @DisplayName("When getOneHash called retrieve one hash from cache, if it's less than fill percent generate hash")
    public void whenGetOneHashCalledThenRetrieveHashFromCacheAndGenerateHashesIfNecessary()
            throws InterruptedException {
        Thread.sleep(1000);

        String resultHashB = hashCache.getOneHash();
        assertEquals("b", resultHashB);
        String resultHashC = hashCache.getOneHash();
        assertEquals("c", resultHashC);
        String resultHashD = hashCache.getOneHash();
        assertEquals("d", resultHashD);
        String resultHashE = hashCache.getOneHash();
        assertEquals("e", resultHashE);
        String resultHashF = hashCache.getOneHash();
        assertEquals("f", resultHashF);

        Thread.sleep(1500);
        int size = hashRepository.findAll().size();

        assertEquals(200, size);
    }
}
