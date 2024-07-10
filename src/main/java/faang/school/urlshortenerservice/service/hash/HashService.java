package faang.school.urlshortenerservice.service.hash;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HashService {

    CompletableFuture<Void> generateHashesBatch();

    List<String> getHashes(Long aLong);

    CompletableFuture<List<String>> getHashesAsync(Long amount);
}
