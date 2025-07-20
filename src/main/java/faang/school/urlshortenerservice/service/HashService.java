package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.config.HashConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {

    private final HashConfig hashConfig;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor executor;
    private final AtomicBoolean refilling = new AtomicBoolean(false);

    @Transactional
    public void refillHashStorage() {
        List<String> newHashValues = hashGenerator.generateHashes(hashConfig.getStorage().getSize());
        List<Hash> newHashes = newHashValues.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(newHashes);
        log.info("{} hashes generated", newHashes.size());
    }

    @Transactional
    public List<String> getFreeHashes(long count) {
        List<String> freeHashes = hashRepository.getFreeHashBatch(count);
        hashRepository.deleteAllByHashIn(freeHashes);
        startRefillIfNeeded();
        return freeHashes;
    }

    private void startRefillIfNeeded() {
        boolean needRefill = hashRepository.count() < hashConfig.getStorageUpdateCount()
                && refilling.compareAndSet(false, true);
        if (needRefill) {
            refillStorageAsync();
        }
    }

    private void refillStorageAsync() {
        executor.submit(() -> {
            try {
                log.info("Start refiling hash storage...");
                refillHashStorage();
            } finally {
                refilling.set(false);
            }
        });
    }
}