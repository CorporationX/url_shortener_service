package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Value("${hash.range:100}")
    private int maxRange;

    @Transactional
    @Scheduled(cron = "${hash.generate-cron}")
    public void generateHash() {
        var range = hashRepository.generatedValues(maxRange);
        var hashes = range.stream().map(this::encode).toList();
        hashRepository.saveHashes(hashes);
    }

    @Transactional
    public List<String> getHashes(int size) {
        List<String> hashes = hashRepository.getHashBatch(size);
        if (hashes.size() < size) {
            generateHash();
            hashes.addAll(hashRepository.getHashBatch(size));
        }
        return hashes;
    }

    @Async("hashExecutor")
    public CompletableFuture<List<String>> getHashesAsync(int size) {
        return CompletableFuture.completedFuture(getHashes(size));
    }

    private String encode(long number) {
        StringBuilder encodedString = new StringBuilder();
        while (number > 0) {
            int num = (int) (number % 62);
            encodedString.append(BASE62_CHARACTERS.charAt(num));
            number /= BASE62_CHARACTERS.length();
        }
        return encodedString.toString();
    }
}