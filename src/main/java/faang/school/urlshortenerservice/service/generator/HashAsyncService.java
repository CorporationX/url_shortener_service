package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashAsyncService {
    private final HashGenerator hashGenerator;

    @Async(value = "threadPool")
    public CompletableFuture<List<Hash>> getHashesAsync(int count) {
        List<Hash> hashes = hashGenerator.getHashes(count);
        return CompletableFuture.completedFuture(hashes);
    }
}
