package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> generateBatch(int n) {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(n);
        List<String> encodedHashes = base62Encoder.encode(uniqueNumbers);
        return CompletableFuture.completedFuture(hashRepository.save(encodedHashes));
    }
}