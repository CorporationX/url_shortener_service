package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static faang.school.urlshortenerservice.exception.ErrorMessage.HASH_GENERATION_FAILED;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private static final String INFO_GENERATE_BATCH = "Generated and saved {} hashes";

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${shortener.hash.batch.size}")
    private int batchSize;

    @Transactional
    public void generateBatch() {
        try {
            List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
            List<String> hashes = base62Encoder.encode(numbers);
            hashRepository.save(hashes);
            log.info(INFO_GENERATE_BATCH, hashes.size());
        } catch (Exception e) {
            log.error(HASH_GENERATION_FAILED, e);
            throw new HashGenerationException(HASH_GENERATION_FAILED);
        }
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<String> hashes = hashRepository.getHashBatch(amount);
        if(hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes;
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(int amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
