package faang.school.urlshortenerservice.service.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCacheAsync {
    private final HashGenerator hashGenerator;

    @Async("hashCacheExecutor")
    public void fillCache(Queue<String> queue, AtomicBoolean isRunning) {
        if (isRunning.compareAndSet(false, true)) {
            try {
                hashGenerator.generateBatch()
                        .thenAccept(queue::addAll)
                        .whenComplete((res, ex) -> isRunning.set(false));
            } catch (Exception e) {
                isRunning.set(false);
            }
        }
    }
}
