package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Component
public class LocalCache {
    private final HashGenerator hashGenerator;

    @Value("${service.cache.capacity:1000}")
    private int capacity;

    @Value("${service.cache.fill.percent}")
    private int fillPercent;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    private final Queue<String> hashes = new ArrayBlockingQueue<>(capacity);


    @PostConstruct
    public void init(){
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash(){
        if(hashes.size() / (capacity/100.0) < fillPercent){
            if(filling.compareAndSet(false, true)){
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(()-> filling.set(false));
            }
        }
        return hashes.poll();
    }
}
