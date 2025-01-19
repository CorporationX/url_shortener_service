package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final TransactionService transactionService;

    @Value("${hashBatch.batchSize:10000}")
    private int batchSize;

    @Scheduled(cron = "${hashGenerator.every-midnight}")
    @Async
    public void generateBatch() {
        transactionService.saveHashBatch(batchSize);
    }

    @Async
    public CompletableFuture<List<String>> getHashBatch(int batchSize) {
        List<String> hashBatch = transactionService.getHashBatch(batchSize);
        if (hashBatch.size() < batchSize) {
            int remaining = batchSize - hashBatch.size();
            generateBatch();
            hashBatch.addAll(transactionService.getHashBatch(remaining));
        }
        return CompletableFuture.completedFuture(hashBatch);
    }
}
