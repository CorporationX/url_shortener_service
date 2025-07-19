package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.encoder.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    @Value("${hash_cache.list_size_hashes}")
    private int maxSizeHashList;

    @Value("${hash_cache.percent}")
    private int percent;

    @Value("${hash_generator.n}")
    private Long n;

    private BlockingQueue<String> hashList;
    private Queue<String> pendingHashes;
    private AtomicBoolean isRefiling;

    private final ExecutorService executorService;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void init() {
        this.isRefiling = new AtomicBoolean(false);
        this.pendingHashes = new ConcurrentLinkedDeque<>();
        this.hashList = new LinkedBlockingQueue<>(maxSizeHashList);
        refillCache();
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }

    public String getHash() {
        if (hashList.size() * 100L / maxSizeHashList <= percent
                && isRefiling.compareAndSet(false, true)) {
            CompletableFuture.runAsync(this::refillCache, executorService)
                    .thenRun(() -> isRefiling.set(false))
                    .exceptionally(ex -> {
                        isRefiling.set(false);
                        return null;
                    });
        }

        return hashList.poll();
    }

    private void refillCache() {
        List<String> hashes = hashRepository.getHashBatch(n);

        List<String> added = hashes.parallelStream()
                .limit(maxSizeHashList - hashList.size())
                .filter(hashList::offer)
                .toList();

        List<String> pending = hashes.parallelStream()
                .skip(added.size())
                .toList();

        pendingHashes.addAll(pending);
        log.info("The list of hashes has been updated");
        CompletableFuture.runAsync(hashGenerator::generateBatch, executorService);
    }

    @Async(value = "customPool")
    @Scheduled(cron = "${hash_cache.cron}")
    public void addPendingHashes() {
        while (!pendingHashes.isEmpty() && hashList.remainingCapacity() > 0) {
            String hash = pendingHashes.poll();
            if (hash != null) {
                hashList.add(hash);
                log.info("Hash {} moved from pendingHashes to hashList", hash);
            }
        }
    }
}
