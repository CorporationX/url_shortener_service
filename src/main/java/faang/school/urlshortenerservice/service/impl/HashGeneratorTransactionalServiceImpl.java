package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGeneratorTransactionalService;
import faang.school.urlshortenerservice.util.Base64Encoder;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class HashGeneratorTransactionalServiceImpl implements HashGeneratorTransactionalService {
    private final Base64Encoder base64Encoder;
    private final HashRepository hashRepository;

    @Autowired
    @Qualifier("hashAsyncExecutor")
    private Executor hashAsyncExecutor;

    @Autowired
    @Qualifier("base64EncodingExecutor")
    private Executor base64EncodingExecutor;

    @Autowired
    @Qualifier("hashGeneratorExecutor")
    private Executor hashGeneratorExecutor;

    public HashGeneratorTransactionalServiceImpl(Base64Encoder base64Encoder, HashRepository hashRepository) {
        this.base64Encoder = base64Encoder;
        this.hashRepository = hashRepository;
    }

    @Override
    @Transactional
    public List<String> getHashes(long requiredAmount, int batchSize) {
        List<Hash> hashes = hashRepository.findAndDelete(requiredAmount);
        do {
            List<Hash> generatedBatch = generateBatch(batchSize);
            hashes.addAll(generatedBatch);
        } while (requiredAmount - hashes.size() > batchSize);

        return hashes.stream()
                .map(Hash::getHash)
                .toList();
    }

    private List<Hash> generateBatch(int batchSize) {
        List<Long> range = hashRepository.getNextRange(batchSize);
        List<CompletableFuture<Hash>> futures = range.stream()
                .map(number -> CompletableFuture.supplyAsync(() -> {
                    String encoded = base64Encoder.applyBase62Encoding(number);
                    return new Hash(encoded);
                }, base64EncodingExecutor))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.join();
        } catch (CompletionException e) {
            log.error("Error in generateBatch", e.getCause());
            throw new RuntimeException("Error generating batch of hashes", e.getCause());
        }

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    @Override
    @Transactional
    public List<String> generateInitBatch(int batchSize) {
        return generateBatch(batchSize).stream()
                .map(Hash::getHash)
                .toList();
    }
}
