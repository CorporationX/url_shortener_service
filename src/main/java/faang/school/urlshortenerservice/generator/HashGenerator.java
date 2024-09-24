package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashService hashService;
    private final Base62Encoder base62Encoder;

    @Value("${spring.generator.count_number}")
    private int countGenerateNumber;

    public List<Hash> generateBatch() {
        List<Long> uniqueNumbers = hashService.getUniqueNumbers(countGenerateNumber);
        List<Hash> hashes = base62Encoder.encodeNums(uniqueNumbers);

        hashService.saveBatch(hashes);
        return hashes;
    }

    @Async("executorService")
    public CompletableFuture<List<Hash>> generateBatchAsync() {
        List<Long> uniqueNumbers = hashService.getUniqueNumbers(countGenerateNumber);
        List<Hash> hashes = base62Encoder.encodeNums(uniqueNumbers);

        hashService.saveBatch(hashes);
        return CompletableFuture.completedFuture(hashes);
    }
}
