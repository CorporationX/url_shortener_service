package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.UrlShortenerApplicationTests;
import faang.school.urlshortenerservice.repository.hash.impl.HashRepositoryImpl;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashGeneratorIT extends UrlShortenerApplicationTests {

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private HashRepositoryImpl hashRepository;

    @Test
    public void generateBatchHashesTest() {
        int batchSize = 50;
        long totalHashesBeforeTest = hashRepository.getHashesCount();

        hashGenerator.generateBatchHashes(batchSize);

        Awaitility.await()
                .atMost(30, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    long totalHashesAfterTest = hashRepository.getHashesCount();
                    assertEquals(batchSize, totalHashesAfterTest - totalHashesBeforeTest);
                });
    }
}
