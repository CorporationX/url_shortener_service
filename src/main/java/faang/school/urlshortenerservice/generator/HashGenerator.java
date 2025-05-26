package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final Lock generationLock = new ReentrantLock();

    @Value("${hash.max-range:1000}")
    private int maxRange;

    @Value("${hash.min-range:100}")
    private int minRange;

    @PostConstruct
    public void validateConfig() {
        if (maxRange <= 0) {
            throw new IllegalStateException("hash.max-range must be positive");
        }
        if (minRange <= 0 || minRange > maxRange) {
            throw new IllegalStateException("hash.min-range must be positive and less than max-range");
        }
    }

    @Transactional
    @Async(value = "hashGeneratorExecutor")
    public void generateHashesAsync() {
        try {
            if (!generationLock.tryLock()) {
                log.debug("Hash generation already in progress");
                return;
            }
            
            try {
                List<Long> range = hashRepository.getNextRange(maxRange);
                if (range.isEmpty()) {
                    log.warn("No available range for hash generation");
                    return;
                }

                List<Hash> hashes = base62Encoder.encode(range).stream()
                        .map(Hash::new)
                        .toList();

                hashRepository.saveAll(hashes);
                log.info("Generated and saved {} hashes", hashes.size());
            } finally {
                generationLock.unlock();
            }
        } catch (Exception e) {
            log.error("Error during hash generation", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> getHashes(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > maxRange) {
            throw new IllegalArgumentException("Requested amount exceeds maximum allowed range");
        }

        List<Hash> hashes = new ArrayList<>();
        try {
            hashes = hashRepository.findAndDelete(amount);
            if (hashes.size() < amount) {
                // Запускаем асинхронную генерацию для будущих запросов
                generateHashesAsync();
                
                if (hashes.size() < amount) {
                    long remaining = amount - hashes.size();
                    List<Hash> additionalHashes = hashRepository.findAndDelete(remaining);
                    if (!additionalHashes.isEmpty()) {
                        hashes.addAll(additionalHashes);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error while retrieving hashes", e);
            return Collections.emptyList();
        }

        if (hashes.size() < amount) {
            log.warn("Could not retrieve requested amount of hashes. Requested: {}, Retrieved: {}", 
                    amount, hashes.size());
        }

        // Если количество оставшихся хешей меньше минимального порога, запускаем генерацию
        long remainingHashes = hashRepository.count();
        if (remainingHashes < minRange) {
            generateHashesAsync();
        }

        return hashes.stream()
                .map(Hash::getHash)
                .toList();
    }

    @Async(value = "hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        try {
            return CompletableFuture.completedFuture(getHashes(amount));
        } catch (Exception e) {
            log.error("Error in async hash retrieval", e);
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
    }
}