package faang.school.urlshortenerservice.generator;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HashGenerator {
    void generateBatch();

    List<String> getHashes(int cashSize);

    CompletableFuture<List<String>> getHashesAsync(int cashSize);
}
