package faang.school.urlshortenerservice.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class AsyncHashGenerator {

    private final HashGenerator hashGenerator;

    @Async("asyncGenerator")
    public CompletableFuture<List<String>> generatedHashAsync() {
        return CompletableFuture.completedFuture(hashGenerator.getHashes());
    }
}
