package faang.school.urlshortenerservice.service.generator.async;

import faang.school.urlshortenerservice.service.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AsyncHashGeneratorImpl implements AsyncHashGenerator {

    private final HashGenerator hashGenerator;

    @Override
    @Async("hashGeneratorTaskExecutor")
    public void generateBatchAsync() {
        hashGenerator.generateBatch();
    }

    @Override
    @Async("hashGeneratorTaskExecutor")
    public CompletableFuture<List<String>> getBatchAsync() {
        return CompletableFuture.completedFuture(hashGenerator.getBatch());
    }
}
