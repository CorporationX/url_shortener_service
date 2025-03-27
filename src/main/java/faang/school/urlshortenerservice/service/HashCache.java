package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashCache {

    private final Queue<String> cachedHashes = new ConcurrentLinkedQueue<>();

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash-cache.max-size}")
    private int QUEUE_SIZE;

    @Value("${hash-cache.refresh-percentage}")
    private String REFRESH_PERCENTAGE;

    // lock on bd level
    public String getHash() {
        int min = (int) (QUEUE_SIZE * Double.parseDouble(REFRESH_PERCENTAGE));
        if (cachedHashes.size() < min) {
            fillHashes();
        }
        String hash = cachedHashes.poll();
        log.info("Polled hash from the queue {}", hash);
        return hash;
    }

    @Async(value = "threadPoolExecutor")
    public void fillHashes() {
        hashGenerator.generateBatch();
        List<String> batch = hashRepository.getHashBatch(QUEUE_SIZE);
        cachedHashes.addAll(batch);
        log.info("{} hashes added to the internal queue", cachedHashes.size());
    }

    @PostConstruct
    public void init() {
        fillHashes();
        log.info("Filled internal cache with hashes. Current size: {}", cachedHashes.size());
    }
}
