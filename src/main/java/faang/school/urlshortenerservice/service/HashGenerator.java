package faang.school.urlshortenerservice.service;

import com.google.common.collect.Lists;
import faang.school.urlshortenerservice.repository.AdvisoryLockRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final AdvisoryLockRepository lockRepository;
    private final PlatformTransactionManager transactionManager;
    @Resource(name = "generateExecutorService")
    private final ExecutorService generateExecutorService;
    @Resource(name = "hashSaveExecutorService")
    private final ExecutorService hashSaveExecutorService;
    @Value("${hash-generator.generate-count:1000}")
    private int generateCounts;
    @Value("${hash-generator.ratio-free-hashes:0.3}")
    private double ratioFreeCaches;
    @Value("${hash-generator.advisory-lock-id:12345}")
    private long lockId;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:1000}")
    private int batchSize;

    @PostConstruct
    public void init() {
        checkHashCounts();
    }

    @AfterReturning(pointcut = "@annotation(faang.school.urlshortenerservice.aop.CheckAndGenerateHash)")
    public void asyncCheckHashCounts() {
        log.info("Check hash counts");
        CompletableFuture.runAsync(() -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.executeWithoutResult(status -> checkHashCounts());
        }, generateExecutorService);
    }

    private void checkHashCounts() {
        int hashCount = (int) hashRepository.count();
        log.info("Hash count: {}", hashCount);
        if (hashCount == 0) {
            generateBatch(generateCounts);
        }
        if ((double) hashCount / generateCounts < ratioFreeCaches && lockRepository.acquireLock(lockId)) {
            generateBatch(generateCounts - hashCount);
        }
    }

    private void generateBatch(int count) {
        log.info("Generating hashes");

        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(count);
        List<String> hashStrings = base62Encoder.encode(uniqueNumbers);

        List<CompletableFuture<List<String>>> futures = Lists.partition(hashStrings, batchSize).stream()
                .map(list ->
                        CompletableFuture.supplyAsync(
                                () -> hashRepository.saveAll(list), hashSaveExecutorService))
                .toList();
        List<String> savedHashes = futures.stream()
                .flatMap(listCompletableFuture ->
                        listCompletableFuture.join().stream())
                .toList();
        log.info("Saved hashes count: {}", savedHashes.size());
    }
}
