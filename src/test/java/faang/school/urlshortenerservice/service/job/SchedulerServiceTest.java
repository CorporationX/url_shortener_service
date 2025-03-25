package faang.school.urlshortenerservice.service.job;

import faang.school.urlshortenerservice.BaseIntegrationTest;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SchedulerServiceTest extends BaseIntegrationTest {
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private HashRepository hashRepository;
    @Autowired
    private ExecutorService hashGenerateExecutorService;

    @Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/insert-old-urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void deleteOldUrl() {
        hashGenerateExecutorService.shutdown();
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> hashGenerateExecutorService.isTerminated());
        hashRepository.deleteAll();
        assertEquals(11, urlRepository.count());
        assertEquals(0, hashRepository.count());

        schedulerService.deleteOldUrl();

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> hashRepository.count() == 11);

        assertEquals(0, urlRepository.count());
        assertEquals(11, hashRepository.count());
    }
}