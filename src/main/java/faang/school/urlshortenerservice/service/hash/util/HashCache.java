package faang.school.urlshortenerservice.service.hash.util;

import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashCache {
    private final HashService hashService;
    private final HashGenerator hashGenerator;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Queue<String> hashes = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);

    @Value("${app.hash_cache.hashes_max_size}")
    private int hashesMax;

    @Value("${app.hash_cache.hashes_min_size}")
    private int hashesMin;

    @PostConstruct
    public void loadHashes() {
        isUpdating.set(true);
        updateHashes();
    }

    public String getHash() {
        checkHashesSize();
        return hashes.poll();
    }

    private void checkHashesSize() {
        if (isUpdating.compareAndSet(false, true)) {
            if (hashes.size() < hashesMin) {
                executor.execute(this::updateHashes);
            } else {
                isUpdating.set(false);
            }
        }
    }

    private void updateHashes() {
        try {
            hashGenerator.generate();
            List<String> newHashes = hashService.findAllByPackSize(hashesMax);
            hashes.addAll(newHashes);
        } finally {
            isUpdating.set(false);
        }
    }
}
