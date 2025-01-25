package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AsyncHashService {
    private final HashService hashService;

    @Async("taskExecutor")
    public CompletableFuture<List<String>> getHashesAsync(int amount) {
        return CompletableFuture.completedFuture(hashService.getHashes(amount));
    }
}
