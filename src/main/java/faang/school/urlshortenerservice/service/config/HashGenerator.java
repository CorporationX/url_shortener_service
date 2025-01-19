package faang.school.urlshortenerservice.service.config;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
@RequiredArgsConstructor
@Data
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    private final ExecutorService customThreadPool;

    @Value("${spring.properties.batch-size}")
    private int batchSize;

    @Async("customThreadPool")
    public void generateBatch() {
        customThreadPool.submit(() -> {
            try {
                List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
                List<Hash> hashes = encoder.encode(uniqueNumbers);
                hashRepository.saveAll(hashes);
            } catch (Exception e) {
                log.error("Could not generate hashes", e);
                throw new RuntimeException(e.getMessage());
            }
        });
    }
}
