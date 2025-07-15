package faang.school.urlshortenerservice.async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncHashGenerator {
    CompletableFuture<List<String>> getHashes();
}
