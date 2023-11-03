package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashJpaRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final HashJpaRepository hashJpaRepository;
    private final HashRepository hashRepository;
    @Value("${uniqueNumbers}")
    private int uniqueNumber;

    @Transactional
    public void generateBatch() {
        Set<Long> uniqueNumbers = hashRepository.getUniqueNumbers(uniqueNumber);
        List<Hash> hashes = uniqueNumbers.stream()
                .map(this::encode)
                .map(Hash::new)
                .collect(Collectors.toList());
        hashJpaRepository.saveBatch(hashes);
        log.info("Hashes was successfully generated {}", hashes);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashBatch = hashRepository.getHashAndDelete(amount);
        if (hashBatch.size() < amount) {
            generateBatch();
            hashBatch.addAll(hashRepository.getHashAndDelete(amount - hashBatch.size()));
        }
        hashJpaRepository.saveBatch(hashBatch);
        return hashBatch.stream()
                .map(Hash::getHash)
                .collect(Collectors.toList());
    }

    @Async("batchExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    private String encode(long number) {
        StringBuilder sb = new StringBuilder();
        do {
            int remainder = (int) (number % 62);
            sb.insert(0, BASE62_CHARS.charAt(remainder));
            number /= 62;
        } while (number > 0);
        return sb.toString();
    }
}
