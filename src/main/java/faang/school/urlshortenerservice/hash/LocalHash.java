package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.scheduler.Scheduler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class LocalHash {
    private final Queue<String> concurrentQueue = new ConcurrentLinkedQueue<>();
    private final HashGenerator hashGenerator;
    private final Scheduler scheduler;

    @Value(value = "${hash.local.minSize:200}")
    private int minSize;

    @Value("${hash.min:500}")
    private int minGetHashes;

    @PostConstruct
    private void init() {
        getHashes();
    }

    @Retryable(
            retryFor = { HashNotFoundException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String getHash() {
        if (concurrentQueue.size() < minSize) {
            CompletableFuture.runAsync(this::getHashes);
        }

        return Optional.ofNullable(concurrentQueue.poll())
                .orElseThrow(() -> new HashNotFoundException("Hash hasn't been initialized yet"));
    }

    private void getHashes() {
        List<String> hashes = hashGenerator.getHashes();

        if (hashes.size() < minGetHashes) {
            scheduler.sendMessage();
        }

        concurrentQueue.addAll(hashes);
    }
}
