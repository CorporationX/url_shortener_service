package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.AdvisoryLockRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final AdvisoryLockRepository lockRepository;
    @Resource(name = "generateExecutorService")
    private final ExecutorService generateExecutorService;
    @Value("${hash-generator.generate-count:1000}")
    private int generateCounts;
    @Value("${hash-generator.ratio-free-hashes:0.3}")
    private double ratioFreeCaches;
    @Value("${hash-generator.advisory-lock-id:12345}")
    private long lockId;

    @PostConstruct
    public void init() {
        log.info("Init check hash counts");
        int hashCount = (int) hashRepository.count();
        log.info("Init hash count: {}", hashCount);

        if ((double) hashCount / generateCounts < ratioFreeCaches && lockRepository.acquireLock(lockId)) {
            generateBatch(generateCounts - hashCount);
        }
    }

    @Transactional
    public void checkHashCountsAsync() {
        log.info("Check hash counts");
        int hashCount = (int) hashRepository.count();
        log.info("Hash count: {}", hashCount);

        if ((double) hashCount / generateCounts < ratioFreeCaches && lockRepository.acquireLock(lockId)) {
            generateExecutorService.submit(() -> generateBatch(generateCounts - hashCount));
        }
    }

    private void generateBatch(int count) {
        log.info("Generating hashes");

        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(count);
        List<String> hashStrings = base62Encoder.encode(uniqueNumbers);
        int savedCount = hashRepository.saveAll(hashStrings);

        log.info("Saved hashes count: {}", savedCount);
    }
}
