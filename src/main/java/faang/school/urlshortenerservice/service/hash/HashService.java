package faang.school.urlshortenerservice.service.hash;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HashService {

    CompletableFuture<Void> generateHashes();

    List<String> getHashes(int amount);

    CompletableFuture<List<String>> getHashesAsync(int amount);
}
