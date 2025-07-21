package faang.school.urlshortenerservice.model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import faang.school.urlshortenerservice.config.MainConfig;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.CustomTaskExecutor;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final CustomTaskExecutor customTaskExecutor;
    private final MainConfig mainConfig;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    // @Value("${hashes.numberOfHashes}")
    // private int numberOfHashes;
    // private ArrayBlockingQueue<String> hashQueue = new ArrayBlockingQueue<>(numberOfHashes);
    private ArrayBlockingQueue<String> hashQueue = new ArrayBlockingQueue<>(10);


    @Async("customTaskExecutor")
    public CompletableFuture<String> getHash() {
        if (ifReachedThreshold()) {
            int requestedHashesNumber = mainConfig.getNumberOfHashes() - hashQueue.size();
            customTaskExecutor.execute(() -> {
                hashGenerator.generateBatch();
                hashQueue.addAll(hashRepository.getHashBatchOf(requestedHashesNumber));
            });
        }

        String returnHash = hashQueue.poll();
        return CompletableFuture.completedFuture(returnHash);
    }

    private boolean ifReachedThreshold() {
        return hashQueue.size() * 100 < mainConfig.getNumberOfHashes() * mainConfig.getMimThresholdPercent();
    }
}
