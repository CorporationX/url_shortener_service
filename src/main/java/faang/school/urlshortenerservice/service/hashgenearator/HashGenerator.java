package faang.school.urlshortenerservice.service.hashgenearator;

import faang.school.urlshortenerservice.repository.HashesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final Base62Encoder base62Encoder;
    private final HashesRepository hashesRepository;

    @Value("${url-shortener-service.hash-generator.hashes-batch-size:100}")
    private long batchSize;

    @Async("hashGeneratorTaskExecutor")
    public CompletableFuture<Boolean> generateBatch() {
        List<Long> numbers = hashesRepository.getUniqueNumbers(this.batchSize).stream().map(BigInteger::longValue).toList();
        List<String> hashes = base62Encoder.encode(numbers);
        hashesRepository.save(hashes.toArray(new String[0]));
        return CompletableFuture.completedFuture(true);
    }
}
