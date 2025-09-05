package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
@Service
public class HashServiceAsync {

    private final HashService hashService;

    @Qualifier("hashCacheExecutor")
    private final Executor hashCacheExecutor;

    public CompletableFuture<List<String>> getHashesAsync(long count) {
        return CompletableFuture.supplyAsync(() -> hashService.getHashes(count), hashCacheExecutor);
    }


}
