package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
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
    @Value("${hash.unique-numbers-amount:10000}")
    private int uniqueNumbersCount;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorThreadPool")
    public CompletableFuture<List<String>> generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(uniqueNumbersCount);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.save(hashes);
        return CompletableFuture.completedFuture(hashes);
    }
}
