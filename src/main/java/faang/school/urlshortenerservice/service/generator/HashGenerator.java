package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.model.Hash;
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
    @Value("${hash-cache.max.range:10000}")
    private int maxRange;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    public void generateHashes() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = range.stream().map(base62Encoder::encode).map(Hash::new).toList();

        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<Hash> getHashes(int count) {
        List<Hash> hashes = hashRepository.findAndDelete(count);
        if (count > hashes.size()) {
            generateHashes();
            hashes.addAll(hashRepository.findAndDelete(count - hashes.size()));
        }
        return hashes;
    }

    @Async
    @Transactional
    public CompletableFuture<List<Hash>> getHashesAsync(int count) {
        List<Hash> hashes = getHashes(count);
        return CompletableFuture.completedFuture(hashes);
    }
}
