package faang.school.urlshortenerservice.model;

import java.util.concurrent.ArrayBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import faang.school.urlshortenerservice.config.MainConfig;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.CustomTaskExecutor;
import faang.school.urlshortenerservice.util.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final CustomTaskExecutor customTaskExecutor;
    private final MainConfig mainConfig;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hashes.numberOfHashes}")
    private int numberOfHashes;
    private ArrayBlockingQueue<String> hashQueue;

    @PostConstruct
    public void init() {
        this.hashQueue = new ArrayBlockingQueue<>(numberOfHashes);
        hashQueue.addAll(hashRepository.getHashBatchOf(numberOfHashes));
    }

    public String getHash() {
        if (ifReachedThreshold()) {
            generateHashBatchAsync();
        }

        return hashQueue.poll();
    }

    private boolean ifReachedThreshold() {
        return hashQueue.size() * 100 < mainConfig.getNumberOfHashes() * mainConfig.getMimThresholdPercent();
    }

    @Async("customTaskExecutor")
    public void generateHashBatchAsync() {
        int requestedHashesNumber = mainConfig.getNumberOfHashes() - hashQueue.size();
        customTaskExecutor.execute(() -> {
            hashGenerator.generateBatch();
            hashQueue.addAll(hashRepository.getHashBatchOf(requestedHashesNumber));
        });
    }
}
