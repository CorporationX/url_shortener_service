package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.config.ConstantsProperties;
import faang.school.urlshortenerservice.util.LockUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue();
    //    private final ExecutorService executorService;
    private final ConstantsProperties constantsProperties;
    private final HashGenerator generator;

    private final ReentrantLock lock = new ReentrantLock();

    public String getHash() {
        return cache.poll();
    }

    @Scheduled(cron = "0/1 * * * * *")
    @Async("taskExecutor")
    public void checkAndRefillFreeHashesLeft() {
        if (cache.size() > (constantsProperties.getCacheGenThreshold())) return;

        LockUtil.withLock(lock, generator::generateBatch);
    }
}
