package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class LocalCache {
    private final HashGenerator hashGenerator;

    @Value("${service.cache.capacity:1000}")
    private final int capacity;

    @Value("${service.cache.fill.percent}")
    private final int fillPercent;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    private final Queue<String> hashes;

    public LocalCache(HashGenerator hashGenerator,
                      @Value("${service.cache.capacity}") int capacity,
                      @Value("${service.cache.fill.percent}") int fillPercent) {
        this.hashGenerator = hashGenerator;
        this.capacity = capacity;
        this.fillPercent = fillPercent;
        this.hashes = new ArrayBlockingQueue<>(capacity);
    }


    @PostConstruct
    public void init(){
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash(){
        if(hashes.size() / (capacity/100.0) < fillPercent && filling.compareAndSet(false, true)){
            int spaceToFill = capacity - hashes.size();
            if(spaceToFill > 0){
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .exceptionally(ex -> {
                            filling.set(false);
                            return null;
                        })
                        .thenRun(() -> filling.set(false));
            }
            else{
                filling.set(false);
            }
        }
        return hashes.poll();
    }
}
