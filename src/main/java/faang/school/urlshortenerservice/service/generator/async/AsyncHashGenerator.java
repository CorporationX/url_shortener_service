package faang.school.urlshortenerservice.service.generator.async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncHashGenerator {

    void generateBatchAsync();

    CompletableFuture<List<String>> getBatchAsync();
}
