package faang.school.urlshortenerservice.service.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {

    private final HashBatchGenerator hashBatchGenerator;

    @Override
    @Async("hashGeneratorExecutorService")
    public CompletableFuture<List<String>> generateBatchAsync() {
        return CompletableFuture.completedFuture(hashBatchGenerator.generateBatch());
    }
}
