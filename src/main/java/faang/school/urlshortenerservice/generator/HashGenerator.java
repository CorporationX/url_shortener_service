package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.batch_size:1000}")
    private int batchSize;


    @Transactional
    public void generatedBatch() {
        List<Long> generatedNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = base62Encoder.encode(generatedNumbers).stream().map(string ->
                Hash.builder().hash(string).build()).collect(Collectors.toList());
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<Hash> getBatch() {
        List<Hash> hashes = hashRepository.getAndDeleteHashBatch(batchSize);
        if (hashes.size() < batchSize) {
            generatedBatch();
            hashes.addAll(hashRepository.getAndDeleteHashBatch(batchSize - hashes.size()));
        }
        return hashes;
    }

    @Async("executorService")
    public CompletableFuture<List<Hash>> getBatchAsync() {
        return CompletableFuture.completedFuture(getBatch());
    }
}