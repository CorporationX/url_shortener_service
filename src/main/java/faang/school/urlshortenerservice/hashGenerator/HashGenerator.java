package faang.school.urlshortenerservice.hashGenerator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    @Value("${hash.generate_batch_size}")
    private int generateBatchSize;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("executorService")
    public CompletableFuture<List<Hash>> generateHash() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(generateBatchSize);

        List<Hash> hashes = uniqueNumbers.stream().map(base62Encoder::encode).map(Hash::new).toList();

        return CompletableFuture.completedFuture(hashes);
    }
}
