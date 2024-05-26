package faang.school.urlshortenerservice.hash.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.hash.encoder.Base62;
import faang.school.urlshortenerservice.repository.hash.CustomHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {
    private final CustomHashRepository customHashRepositoryImpl;
    private final Base62 base62Encoder;
    @Value("${hashgenerator.batch-size}")
    private int batchSize;

    @Override
    public void generateBatch() {
        List<Long> numbers = customHashRepositoryImpl.getUniqueNumbers(batchSize);
        customHashRepositoryImpl.save(base62Encoder.encode(numbers));
    }

    @Async("threadPoolTaskExecutor")
    @Override
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    @Override
    public List<String> getHashes(long amount) {
        List<Hash> hashes = customHashRepositoryImpl.getHashBatch(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(customHashRepositoryImpl.getHashBatch(amount - hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).collect(Collectors.toList());
    }
}
