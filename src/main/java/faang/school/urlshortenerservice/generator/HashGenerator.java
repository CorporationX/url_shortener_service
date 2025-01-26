package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private static final String BASE62 = "absdefghijklmnopqrstuvwxyzABSDEFGHIJKLMNOPRSTUVWXYZ0123456789";
    private final HashRepository hashRepository;

    @Value("${hash.range: 10}")
    private int maxRange;

    @Transactional
    public void generateBatch() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = range.stream()
                .map(this::applyBase62Encoding)
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }

//    @Transactional
//    public List<Hash> getHashes(long amount) {
//        List<Hash> hashes = hashRepository.getHashes(amount);
//        if (hashes.size() < amount) {
//            generateBatch();
//        }
//        return hashes;
//    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashes = hashRepository.getHashes(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.getHashes(amount-hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    private String applyBase62Encoding(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            stringBuilder.append(BASE62.charAt((int) (number % BASE62.length())));
            number /= BASE62.length();
        }
        return stringBuilder.toString();
    }
}
