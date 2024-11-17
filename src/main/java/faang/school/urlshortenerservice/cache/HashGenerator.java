package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.BlockingQueue;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash-generator.batch-size}")
    private final int batchSize;

    @Transactional
    public void fillHashCache(BlockingQueue<String> queueHash, int batchSize) {
        List<String> hashes = hashRepository.getHashBatch(batchSize).stream()
            .map(Hash::getHash)
            .toList();
        if (hashes.isEmpty()) {
            throw new RuntimeException("There are no hashes in the database");
        }
        hashRepository.deleteByIds(hashes);
        generateBatch();
        queueHash.addAll(hashes);
        log.info("{} hashes added to the queue", hashes);
    }

    @Async("hashGeneratorExecutor")
    @PostConstruct
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> stringHashes = base62Encoder.encode(numbers);
        List<Hash> hashes = stringHashes.stream()
            .map(string -> Hash.builder().hash(string).build())
            .toList();
        hashRepository.saveAll(hashes);

        log.info("{} hashes added to the table", hashes.size());
    }
}
