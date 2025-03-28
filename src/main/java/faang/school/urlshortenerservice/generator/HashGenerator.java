package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${data.url.hash.maxRange}")
    private int maxRange;

    @Value("${data.url.hash.hash-batch-size}")
    private int hashBatchSize;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(maxRange);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        List<Hash> hashEntities = hashes.stream()
                .map(hash -> Hash.builder().hash(hash).build())
                .collect(Collectors.toList());
        hashRepository.saveAll(hashEntities);
    }

    @Transactional
    public List<Hash> getHashes() {
        List<Hash> hashes = new ArrayList<>(hashRepository.getHashBatch(hashBatchSize));
        if (hashes.size() < hashBatchSize) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(hashBatchSize));
        }
        return hashes;
    }
}