package faang.school.urlshortenerservice.hash.generator;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HashGenerator {
    void generateBatch();

    CompletableFuture<List<String>> getHashesAsync(long amount);

    List<String> getHashes(long amount);
}
