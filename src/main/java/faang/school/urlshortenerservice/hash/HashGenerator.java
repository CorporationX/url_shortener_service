package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepositoryJpa;
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
    private final HashRepositoryJpa hashRepositoryJpa;
    private final Encoder base62Encoder;
    @Value("${hashRepository.batchSize}")
    private int batchSize;


    @Transactional
    @Async(value = "executorService")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepositoryJpa.getUniqueNumbers(batchSize);
        List<String> encodes = base62Encoder.encode(uniqueNumbers);
        List<Hash> hashes = generateHashes(encodes);
        hashRepositoryJpa.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<String> hashes = hashRepositoryJpa.getHashBatch(amount);
        if(hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepositoryJpa.getHashBatch(amount-hashes.size()));
        }
        return hashes;
    }

    public CompletableFuture<List<String>> generatedBatchAsync(int amount){
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    private List<Hash> generateHashes(List<String> encodes) {
        return encodes.stream().map(hash -> new Hash(hash)).toList();
    }
}
