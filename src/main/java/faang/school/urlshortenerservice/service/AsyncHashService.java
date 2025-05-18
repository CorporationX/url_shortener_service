package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.hash.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncHashService {

    private final HashGenerator hashGenerator;
    private final Queue<String> hashes = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean isFilling = new AtomicBoolean(true);

    @Async("asyncExecutor")
    public void fillHashCacheAsync() {
        try {
            List<String> newHashes = hashGenerator.getHashes();
            hashes.addAll(newHashes);
        } catch (HashGenerationException ex) {
            log.error("Failed to fill hash cache asynchronously", ex);
        } finally {
            isFilling.set(false);
        }
    }
}
