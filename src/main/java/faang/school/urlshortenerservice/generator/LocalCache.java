package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalCache {
    @Value("${app.local_hash.capacity:10000}")
    private int capacity;
    @Value("${app.local_hash.min:100}")
    private int minValue;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    private final HashGenerator hashGenerator;
    private final Queue<String> hashes = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void init(){
      hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash(){
        if (hashes.size() < minValue){
            refill();
        }
        return hashes.poll();
    }

    private void refill(){
        if (isRefilling.compareAndSet(false, true)){
            hashGenerator.getHashesAsync(capacity).thenAccept(newHashes -> {
                hashes.addAll(newHashes);
                isRefilling.set(false);
                log.info("Successfully refilled LocalCache with {} hashes", newHashes.size());
            }).exceptionally(ex -> {
                isRefilling.set(false);
                log.error("Error refilling LocalCache: ", ex);
                return null;
            });
        }
    }
}
