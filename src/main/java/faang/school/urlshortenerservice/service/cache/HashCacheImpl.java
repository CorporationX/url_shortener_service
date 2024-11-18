package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCacheImpl implements HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService executorService;
    private final Queue<String> hashes = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean isReplenishing = new AtomicBoolean(false);

    @Value("${server.hash.fetch.batch.size}")
    private int fetchHashesSize;
    @Value("${server.hash.fetch.batch.min-size-percentage}")
    private int hashesMinSizeAsPercentage;

    @PostConstruct
    public void initFreeHashes() {
        fetchFreeHashes();
    }

    @Override
    public String getHash() {
        if (hashes.size() <= (fetchHashesSize * hashesMinSizeAsPercentage / 100.0) && isReplenishing.compareAndSet(false, true)) {
            fetchFreeHashes();
        }
        return Optional.ofNullable(hashes.poll())
                .or(hashRepository::getHash)
                .orElseThrow(() -> new RuntimeException("Free hash not found!"));
    }

    private void fetchFreeHashes() {
        executorService.execute(() -> {
            List<String> newFreeHashes = hashRepository.getHashBatch(fetchHashesSize);
            hashes.addAll(newFreeHashes);
            hashGenerator.generateBatch();
            isReplenishing.set(false);
        });
    }
}
