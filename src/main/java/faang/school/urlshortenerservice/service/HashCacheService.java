package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashCacheService {

    private final HashGenerator hashGenerator;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Value("${hash-cache-setting.queue-size}")
    private int size;

    @Value("${hash-cache-setting.percentage-to-generate-new-hashes}")
    private int percentageToGenerateNewHashes;

    private Queue<String> hashesCacheQueue;

    @PostConstruct
    public void init() {
        hashesCacheQueue = new ArrayBlockingQueue<>(size);
        hashesCacheQueue.addAll(hashGenerator.generateHashes());
    }

    public String getHash() {
        if (hashesCacheQueue.size() / size * 100 < percentageToGenerateNewHashes) {
            if (isRunning.compareAndSet(false, true)) {
                hashGenerator.generateHashes();
                isRunning.set(false);
            }
        }
        return hashesCacheQueue.poll();
    }
}
