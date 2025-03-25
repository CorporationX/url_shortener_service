package faang.school.url_shortener_service.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class AsynchronousHashGenerator {
    private final HashGenerator hashGenerator;

    @Value("${executor.hash-generation.core-pool-size}")
    private int corePoolSize;

    public CompletableFuture<List<String>> getHashesAsynchronously(int amount) {
        return CompletableFuture.supplyAsync(() -> {
            List<CompletableFuture<List<String>>> tasks = new ArrayList<>();
            for (int i = 0; i < corePoolSize; i++) {
                tasks.add(hashGenerator.generateHashesAsync(amount / corePoolSize));
            }
            return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
                    .thenApply(v -> tasks.stream()
                            .flatMap(future -> {
                                try {
                                    return future.get().stream();
                                } catch (Exception e) {
                                    log.error("Error while retrieving async hash batch", e);
                                    return Stream.empty();
                                }
                            }).toList()
                    ).join();
        });
    }
}