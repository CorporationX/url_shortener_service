package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    public static final String BASE_62_CHARACTER =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final HashRepository hashRepository;
    @Value("${hash.range:10000}")
    private int maxRange;
    @Value("${hash.generator.hash_length:6}")
    private int hashLength;

    @Transactional
    @Scheduled(cron = "${hash.cron:0 0 0 * * *}")
    public void generateBatch() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashesToInsert = range.stream()
                .map(hash -> applyBase62Encoding(maxRange))
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashesToInsert);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Transactional
    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return CompletableFuture.completedFuture(hashes.stream().map(Hash::getHash).toList());
    }

    private String applyBase62Encoding(int borders) {
        StringBuilder builder = new StringBuilder();
        while (builder.length() < hashLength) {
            int num = new SecureRandom().nextInt(1,borders);
            builder.append(BASE_62_CHARACTER.charAt(num % BASE_62_CHARACTER.length()));
        }
        return builder.toString();
    }

}