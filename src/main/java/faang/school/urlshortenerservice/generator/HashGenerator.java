package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
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

    @Value("${data.hash.max-range}")
    private int maxRange;

    @Value("${data.hash.batch-size}")
    private int batchSize;

    @Async("asyncExecutor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(maxRange);
        List<String> hashList = encoder.encode(uniqueNumbers);
        List<Hash> hashes = hashList.stream()
                .map(hash -> Hash.builder().hash(hash).build())
                .toList();
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes() {
        List<Hash> hashes = hashRepository.getHashBatch(batchSize);

        if (hashes.size() < batchSize) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(batchSize));
        }

        return hashes.stream()
                .map(Hash::toString)
                .toList();
    }
}
