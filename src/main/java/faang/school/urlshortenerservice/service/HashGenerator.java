package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.AdvisoryLockRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final AdvisoryLockRepository lockRepository;
    @Value("${hash-generator.generate-count:1000}")
    private int generateCounts;
    @Value("${hash-generator.ratio-free-hashes:0.3}")
    private double ratioFreeCaches;
    @Value("${hash-generator.advisory-lock-id:12345}")
    private long lockId;

    @PostConstruct
    public void init() {
        log.info("Init check hash counts");
        checkHashCounts();
    }

    @Transactional
    public void checkHashCountsAsync() {
        log.info("Check hash counts");
        checkHashCounts();
    }

    private void checkHashCounts() {
        int hashCount = (int) hashRepository.count();
        log.info("Hash count: {}", hashCount);

        if (needsGenerate(hashCount)) {
            generateBatch(generateCounts - hashCount);
        }
    }

    private void generateBatch(int count) {
        log.info("Generating hashes");

        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(count);
        List<String> hashStrings = base62Encoder.encode(uniqueNumbers);
        int savedCount = hashRepository.saveAll(hashStrings);

        log.info("Saved hashes count: {}", savedCount);
    }

    private boolean needsGenerate(double hashCount) {
        boolean isCacheUnderLimit = hashCount / generateCounts < ratioFreeCaches;
        return isCacheUnderLimit && lockRepository.acquireLock(lockId);
    }
}
