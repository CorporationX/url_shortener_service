package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@Data
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generator.max_range:100}")
    private int maxRange;

    @Transactional
    public void generateHashes() {
        List<Long> numbers = hashRepository.getNextRange(maxRange);
        if (numbers.isEmpty()) {
            log.warn("No numbers received from sequence");
            return;
        }
        List<String> hashes = base62Encoder.encode(numbers);
        saveHashes(hashes);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (amount == 0) {
            return List.of();
        }

        List<Hash> hashes = new ArrayList<>(hashRepository.findAndDelete(amount));
        if (hashes.size() < amount) {
            log.info("Not enough hashes available ({} of {}), generating new ones", hashes.size(), amount);
            generateHashes();
            List<Hash> additionalHashes = hashRepository.findAndDelete(amount - hashes.size());
            hashes.addAll(additionalHashes);

            if (hashes.size() < amount) {
                log.warn("Still not enough hashes after generation ({} of {})", hashes.size(), amount);
            }
        }

        return hashes.stream()
                .map(Hash::getHash)
                .toList();
    }

    @Async("hashGeneratorExecutorService")
    @Transactional
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHashes(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            log.warn("Attempt to save empty or null hashes list");
            return;
        }
        log.info("Saving {} hashes", hashes.size());
        List<Hash> hashEntities = hashes.stream()
                .map(hash -> Hash.builder().hash(hash).build())
                .toList();
        hashRepository.saveAll(hashEntities);
    }
}