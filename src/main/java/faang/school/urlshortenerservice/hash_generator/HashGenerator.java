package faang.school.urlshortenerservice.hash_generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash-generator.batch_size:10000}")
    @SuppressWarnings("unused")
    private int batchSize;

    @Transactional
    @Scheduled(cron = "${hash-generator.running-timetable:0 0 0 * * *}")
    public List<String> generateBatch() {
        var hashes = base62Encoder.encode(hashRepository.getUniqueNumbers(batchSize));
        var hashEntities = hashes.stream()
                .map(hash -> Hash.builder().hash(hash).build())
                .toList();
        hashRepository.saveAll(hashEntities);

        log.debug("{} hashes have been generated and saved to DB", hashes.size());

        return hashes;
    }

    @Transactional
    public List<String> getHashes(long amount) {
        var hashes = hashRepository.getHashBatch(amount);

        while (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }

        return hashes.stream().map(Hash::getHash).toList();
    }

    @Async("hashGeneratorExecutorService")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
