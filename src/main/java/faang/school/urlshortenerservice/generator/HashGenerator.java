package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.service.HashGeneratorTransactionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class HashGenerator {
    private final HashGeneratorTransactionalService hashGeneratorTransactionalService;

    @Autowired
    @Qualifier("hashAsyncExecutor")
    private Executor hashAsyncExecutor;

    @Autowired
    @Qualifier("base64EncodingExecutor")
    private Executor base64EncodingExecutor;

    @Autowired
    @Qualifier("hashGeneratorExecutor")
    private Executor hashGeneratorExecutor;

    public HashGenerator(HashGeneratorTransactionalService hashGeneratorTransactionalService) {
        this.hashGeneratorTransactionalService = hashGeneratorTransactionalService;
    }

    @Async("hashAsyncExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long requiredAmount, int batchSize) {
        try {
            List<String> result = hashGeneratorTransactionalService.getHashes(requiredAmount, batchSize);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Error in getHashesAsync", e);
            CompletableFuture<List<String>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }
}
