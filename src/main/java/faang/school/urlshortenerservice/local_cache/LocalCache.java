package faang.school.urlshortenerservice.local_cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class LocalCache {
    private final HashGenerator hashGenerator;
    private final Queue<Hash> cache;
    private final AtomicBoolean aBoolean;
    private final ExecutorService executorService;

    @Value("${hash.get_size}")
    private long getSize;
    @Value("${hash.min_cache_rep_size}")
    private long minSize;

    public LocalCache(HashGenerator hashGenerator, Queue<Hash> cache,
                      @Qualifier("localHashAtomicBoolean") AtomicBoolean aBoolean,
                      ExecutorService executorService) {
        this.hashGenerator = hashGenerator;
        this.cache = cache;
        this.aBoolean = aBoolean;
        this.executorService = executorService;
    }

    @PostConstruct
    public void init() {
        log.info("add new hash from db to local hash as initialization");
        addNewHash();
    }

    public String getCache() {
        if (cache.size() - 1 < minSize) {
            if (aBoolean.compareAndExchange(true, false)) {

                executorService.execute(()->{

                    log.info("start new Thread to generate local hash");
                    addNewHash();
                    aBoolean.set(true);

                });
            }
        }
        return cache.poll().getHash();
    }

    public void addNewHash() {
        log.info("calling hashGenerator to get new hash from db and add it to local hash");
        cache.addAll(hashGenerator.findAndDelete());
    }
}
