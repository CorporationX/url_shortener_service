package faang.school.urlshortenerservice.hashservice;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.quantity-generated-numbers}")
    private int quantityNumbers;

    @Transactional
    @Async("getAsyncExecutor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(quantityNumbers);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.save(hashes);
        log.info("Generated {} hashes", quantityNumbers);
    }

    @Transactional
    public List<String> getHashes(long quantity) {
        while (hashRepository.getHashesSize() < quantity) {
            generateBatch();
        }
        return hashRepository.getHashBatch(quantity);
    }

    @Transactional
    @Async("getAsyncExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long quantity) {
        return CompletableFuture.completedFuture(getHashes(quantity));
    }
}
