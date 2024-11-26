package faang.school.urlshortenerservice.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HashService {

    List<Long> getUniqueNumbers(int number);

    void saveBatch(List<String> hashes);

    CompletableFuture<List<String>> getHashes(int number);
}