package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;

    @Value("${hash.range}")
    private int maxRange;

    @Transactional
    @Scheduled(cron = "${hash.cron.generate-hash}")
    public void generateHash() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = range.stream().map(this::applyBase62Encode).map(Hash::new).toList();
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<Hash> getHashes(long amount) {
        List<Hash> hashes = new ArrayList<>(hashRepository.getHashBatch(amount));
        if (hashes.size() < amount) {
            generateHash();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes;
    }

    @Async("executorHashGenerator")
    public CompletableFuture<List<Hash>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    private String applyBase62Encode(Long input) {
        char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        int BASE = BASE62.length;

        StringBuilder encoded = new StringBuilder();
        while (input > 0) {
            int remainder = (int) (input % BASE);
            encoded.append(BASE62[remainder]);
            input /= BASE;
        }
        return encoded.reverse().toString();
    }
}
