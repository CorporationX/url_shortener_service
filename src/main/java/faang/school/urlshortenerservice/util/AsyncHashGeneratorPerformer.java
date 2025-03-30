package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.service.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class AsyncHashGeneratorPerformer {
    private final HashGenerator hashGenerator;

    @Async("hashGeneratorExecutor")
    public CompletableFuture<Void> generateHashes() {
        return hashGenerator.generateBatches();
    }
}
