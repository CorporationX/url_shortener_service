package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.HashSaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    @Value("${uniqueNumbers}")
    private int uniqueNumbers;
    private final HashRepository repository;
    private final HashSaveRepository saveRepository;
    private final Base62Encoder encoder;

    @Transactional
    public void generateHash() {
        List<Long> unique = repository.getUniqueNumbers(uniqueNumbers);
        List<Hash> hashes = unique
                .stream()
                .map(uniq -> encoder.encode(uniq))
                .map(uniq -> new Hash(uniq))
                .toList();
        saveRepository.save(hashes);
        log.info("Hashes generated successfully {}", hashes);
    }

    @Transactional
    @Async("batchExecutor")
    public CompletableFuture<List<Hash>> getHashes(long amount) {
        List<Hash> hashes = repository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateHash();
            hashes.addAll(repository.findAndDelete(amount - hashes.size()));
        }
        saveRepository.save(hashes);
        return CompletableFuture.completedFuture(hashes);
    }
}
