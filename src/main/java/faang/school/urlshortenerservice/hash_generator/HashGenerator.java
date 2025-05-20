package faang.school.urlshortenerservice.hash_generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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

    @Async("hashGeneratorExecutorService")
    public CompletableFuture<List<String>> generateBatchAsync() {
        return CompletableFuture.supplyAsync(this::generateBatch);
    }

    public List<String> generateBatch() {
        var hashes = base62Encoder.encode(hashRepository.getUniqueNumbers(batchSize));
        var hashEntities = hashes.stream()
                .map(hash -> Hash.builder().hash(hash).build())
                .toList();
        hashRepository.saveAll(hashEntities);

        log.debug("{} hashes have been generated and saved to DB", hashes.size());

        return hashes;
    }
}
