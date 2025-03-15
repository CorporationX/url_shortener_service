package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
public abstract class HashCacheAbstract {
    protected final Queue<Hash> queue;
    protected final UrlShortenerProperties properties;
    private final ExecutorService executorService;
    private final HashService hashService;
    private final HashGenerator hashGenerator;

    public Hash getHash() {
        if (queue.isEmpty()) {
            refillCacheIfNeeded();
        }
        executorService.submit(this::refillCacheIfNeeded);
        return getHashAndGenerate();
    }

    public abstract Hash getHashAndGenerate();

    public synchronized void refillCacheIfNeeded() {
        log.info("Start refillQueueIfNeeded");
        int percentOfFullQueue = getPercentOfFullQueue();
        log.info("Percent Of FullQueue {}", percentOfFullQueue);

        if (queue.isEmpty() || percentOfFullQueue < properties.getMinimumHashLengthInPercent()) {
            List<Hash> newHashes = hashService.getBatchHashesAndDelete();
            queue.addAll(newHashes);
            log.info("Cache is refilled, size of new hashes: {}", newHashes.size());
        }
        hashGenerator.generateBatch();

        log.info("Finish refillQueueIfNeeded");
    }

    private int getPercentOfFullQueue() {
        return queue.size() / properties.getBatchSize() * 100;
    }

//    @PostConstruct
//    public void fillCache() {
//        hashGenerator.generateBatch();
//    }
}
