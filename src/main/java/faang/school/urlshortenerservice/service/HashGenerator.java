package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.entity.Hash;
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
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.range:10000}")
    private int maxRange;

    @Transactional
    public void generateBatch () {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashList = base62Encoder.encode(uniqueNumbers);
        hashRepository.saveAll(hashList);
    }

    @Transactional
    @Async("generatorThreadPool")
    public CompletableFuture<List<String>> getHashes(long amount) {
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return CompletableFuture.completedFuture(hashes.stream().map(Hash::getHash).toList());
    }
}
