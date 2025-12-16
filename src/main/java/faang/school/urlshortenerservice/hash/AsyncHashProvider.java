package faang.school.urlshortenerservice.hash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncHashProvider {
    private final HashGenerator hashGenerator;

    @Async
    public CompletableFuture<List<String>> getHashes(int amount) {
        return CompletableFuture.completedFuture(hashGenerator.getHashes(amount));
    }
}
