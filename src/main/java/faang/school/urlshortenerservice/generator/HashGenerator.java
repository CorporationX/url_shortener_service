package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashService transactionService;
    @Value("${hashBatch.batchSize}")
    private int batchSize;

    @Scheduled(cron = "${hashGenerator.every-midnight}")
    public void generateHashes() {
        transactionService.saveHashBatch(batchSize);
    }

    @Transactional
    public List<String> getHashes(int batchSize) {
        List<String> hashes = new ArrayList<>(transactionService.removeAndGetHashes(batchSize));
        if (hashes.size() < batchSize) {
            int remaining = batchSize - hashes.size();
            transactionService.saveHashBatch(remaining);
            hashes.addAll(transactionService.removeAndGetHashes(remaining));
        }
        return hashes;
    }
}
