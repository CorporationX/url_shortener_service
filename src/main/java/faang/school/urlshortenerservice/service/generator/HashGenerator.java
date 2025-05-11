package faang.school.urlshortenerservice.service.generator;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HashGenerator {
    CompletableFuture<List<String>> generateBatch();
}
