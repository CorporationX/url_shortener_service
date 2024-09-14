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

    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final HashRepository hashRepository;

    @Value("${hash.range}")
    private int maxRange;

    @Transactional
    public void generateHash() {
        List<Long> range = hashRepository.getNextRange();
        List<Hash> hashes = range.stream()
                .map(this::applyBase62Encoding)
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateHash();
            hashes.addAll(hashRepository.findAndDelete(amount));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Transactional
    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    public String applyBase62Encoding(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
            number /= BASE_62_CHARACTERS.length();
        }
        return builder.toString();
    }
}
