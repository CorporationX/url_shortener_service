package faang.school.urlshortenerservice.event;

import faang.school.urlshortenerservice.service.HashCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class ApplicationStartup {
    private final HashCache hashCache;
    private final Executor hashCacheTaskExecutor;

    public ApplicationStartup(
            HashCache hashCache,
            @Qualifier("hashCacheTaskExecutor") Executor hashCacheTaskExecutor) {
        this.hashCache = hashCache;
        this.hashCacheTaskExecutor = hashCacheTaskExecutor;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        hashCacheTaskExecutor.execute(hashCache::fillHashes);
    }
}
