package faang.school.urlshortenerservice.generate;

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

    @Value("${unique_numbers}")
    private long uniqueNumbers;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorPool")
    @Transactional
    public void generateBatch() {
        List<Long> listUniqueNumbers = hashRepository.getUniqueNumbers(uniqueNumbers);
        List<Hash> encode = base62Encoder.encode(listUniqueNumbers).stream()
                .map(Hash::new)
                .toList();
        hashRepository.save(encode);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes.stream()
                .map(Hash::toString)
                .toList();
    }

    @Transactional
    @Async("hashGeneratorPool")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
