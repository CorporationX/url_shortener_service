package faang.school.urlshortenerservice.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HashService {

    void generateBatch();

    List<String> getHashes(Long amount);

    CompletableFuture<List<String>> getHashesAsync(Long amount);

    void saveAllHashes(List<String> hashes);
}
