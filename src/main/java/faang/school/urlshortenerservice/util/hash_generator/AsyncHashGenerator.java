package faang.school.urlshortenerservice.util.hash_generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class AsyncHashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("urlHashTaskExecutor")
    public CompletableFuture<Void> generateAndSaveHashBatch(List<Long> uniqueNumbers) {
        List<String> urlHashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.save(urlHashes);
        return CompletableFuture.completedFuture(null);
    }
}
