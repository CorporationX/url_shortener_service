package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.common.encoder.Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class HashGeneratorImpl implements HashGenerator {
    @Value("${shortener.hash.generator.quantity}")
    private int quantity;

    private final HashRepository repository;
    private final Encoder encoder;

    @Override
    @Transactional
    public List<String> generateBatch() {
        List<Long> uniqueNumbers = repository.getUniqueNumbers(quantity);
        List<String> hashes = encoder
                .encode(uniqueNumbers);
        if (!uniqueNumbers.isEmpty()) {
            repository.saveAll(
                    hashes.stream().map(Hash::new).toList()
            );
        }
        return hashes;
    }

    @Override
    @Transactional
    @Async("hashGeneratorExecutorService")
    public CompletableFuture<List<String>> generateBatchAsync() {
        List<String> hashes = generateBatch();
        return CompletableFuture.completedFuture(hashes);
    }
}
