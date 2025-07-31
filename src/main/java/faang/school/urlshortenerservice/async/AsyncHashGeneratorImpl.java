package faang.school.urlshortenerservice.async;

import faang.school.urlshortenerservice.config.properties.HashGenerationProperties;
import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class AsyncHashGeneratorImpl implements AsyncHashGenerator {
    private final HashGenerator hashGenerator;
    private final HashGenerationProperties hashGenerationProperties;

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashes() {
        return CompletableFuture.supplyAsync(() -> hashGenerator.fetchHashes(
                hashGenerationProperties.getAmountToPull()
        ));
    }
}
