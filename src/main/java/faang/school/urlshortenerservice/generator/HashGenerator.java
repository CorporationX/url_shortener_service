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

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${hash.range:1000}")
    private int range;

    @Transactional
    public void generateHashes() {
        List<Long> nextRange = hashRepository.getUniqueNumbers(range);
        List<String> hashes = encoder.encode(nextRange);
        hashRepository.save(hashes);
    }

    @Transactional
    @Async("hashGenExecutor")
    public CompletableFuture<List<Hash>> getHashes() {
        List<String> hashes = hashRepository.getHashBatch();
        return CompletableFuture.completedFuture(hashes.stream()
                .map(Hash::new)
                .toList());
    }

    public List<Hash> getStartHashes(int amount) {

    }
}
