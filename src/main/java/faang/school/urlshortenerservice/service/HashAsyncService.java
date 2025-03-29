package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashAsyncService {
    private final HashService hashService;

    @Async("hashGeneratorThreadPool")
    public CompletableFuture<List<Hash>> getHashesAsync(long count) {
        return CompletableFuture.completedFuture(hashService.getHashes(count));
    }
}
