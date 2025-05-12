package faang.school.urlshortenerservice.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class AsyncHashGenerator {
    private final HashGenerator hashGenerator;

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getBatchAsync(int uniqueNumbersBatch) {
        return CompletableFuture.completedFuture(hashGenerator.getBatch(uniqueNumbersBatch));
    }
}
