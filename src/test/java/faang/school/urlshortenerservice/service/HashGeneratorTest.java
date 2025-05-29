package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.ContainersConfiguration;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ContainersConfiguration.class)
@DisplayName("HashGenerator Test")
class HashGeneratorTest {

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private HashRepository hashRepository;

    @Test
    @Transactional
    @DisplayName("Generate hashes asynchronously")
    void checkAndGenerateHashesAsync() {
        hashRepository.getHashes((int) hashRepository.getCountOfHashes());

        hashGenerator.checkAndGenerateHashesAsync();

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> hashRepository.getCountOfHashes() > 0);

        long count = hashRepository.getCountOfHashes();
        assertTrue(count > 0, "Hashes should be generated");
    }
}