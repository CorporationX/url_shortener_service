package faang.school.urlshortenerservice.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGeneratorAsync {
    private final HashGenerator hashGenerator;

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getBatchAsync(int n) {
        return CompletableFuture.supplyAsync(() -> hashGenerator.getBatch(n));
    }
}
