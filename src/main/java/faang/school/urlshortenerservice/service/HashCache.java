package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final AtomicBoolean generateIsProcessing = new AtomicBoolean(false);
    private final Queue<String> hashes = new LinkedBlockingQueue<>();

    @Value("${hash.capacity}")
    private int capacity;

    @Value("${hash.min-percent-hashes}")
    private long minPercentHashes;

    @PostConstruct
    public void init() {
        checkAndRunGenerate();
    }

    public String getHash() {
        checkAndGenerateHashes();
        return hashes.poll();
    }

    @Async("ThreadAsyncExecutor")
    public void checkAndGenerateHashes() {
        double cacheFullPercentage = 100.0 / capacity * hashes.size();
        if (cacheFullPercentage <= minPercentHashes) {
            checkAndRunGenerate();
        }
    }

    private void checkAndRunGenerate() {
        if (generateIsProcessing.compareAndSet(false, true)) {
            log.info("Starting generate new hashes for short urls");
            List<String> generatedHashes = hashGenerator.getHashes(capacity);
            hashes.addAll(generatedHashes);
            generateIsProcessing.set(false);
        }
    }
}
