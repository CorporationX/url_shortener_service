package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.hash.HashConfig;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class HashServiceImpl implements HashService {
    @Value("${app.hash.batch.limit}")
    private int limit;
    private final HashRepository hashRepository;
    private final HashConfig hashConfig;
    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor executor;
    private final static String STORAGE_TYPE = "DB";
    private final AtomicBoolean blocked = new AtomicBoolean(false);

    public HashServiceImpl(HashRepository hashRepository, HashConfig hashConfig,
                           HashGenerator hashGenerator, @Qualifier("taskExecutorHashCache") ThreadPoolTaskExecutor executor) {
        this.hashRepository = hashRepository;
        this.hashConfig = hashConfig;
        this.hashGenerator = hashGenerator;
        this.executor = executor;
    }

    @Override
    @Transactional
    public List<Hash> saveAllHashes(List<Hash> hashes) {
        return hashRepository.saveAll(hashes);
    }

    @Override
    @Transactional
    public List<String> getFreeHashes() {
        replenishStorageAutomatically();
        return hashRepository.getHashBatch(limit);
    }

    @Override
    public void refillHashAsync(Runnable runnable, String storageType, AtomicBoolean block) {
        executor.submit(() -> {
            try {
                runnable.run();
            } catch (Exception exception) {
                log.error("Failed to refill");
            } finally {
                block.set(false);
            }
        });
    }

    public void replenishStorageAutomatically() {
        boolean isCheckSize = checkSize() && blocked.compareAndSet(false, true);
        if (isCheckSize) {
            refillHashAsync(hashGenerator::generatedHash, STORAGE_TYPE, blocked);
        }
    }

    public boolean checkSize() {
        return hashRepository.count() < hashConfig.getCurrentOccupancyStorage();
    }

}
