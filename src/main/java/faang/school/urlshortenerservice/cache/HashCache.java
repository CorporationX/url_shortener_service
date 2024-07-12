package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {

    private final int capacity;
    private final int minPercentageToFill;
    private final BlockingQueue<String> hashes;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean filling = new AtomicBoolean();

    @PostConstruct
    public void init(){
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    @Autowired
    public HashCache(@Value("${services.cache.capacity}") int capacity,
                     @Value("${services.cache.minPercentageToFill}")int minPercentageToFill,
                     HashGenerator hashGenerator) {
        this.capacity = capacity;
        this.minPercentageToFill = minPercentageToFill;
        this.hashes = new ArrayBlockingQueue<>(capacity);
        this.hashGenerator = hashGenerator;
    }

    public String getHash(){
        if(hashes.size() / (capacity / 100.0) < minPercentageToFill && filling.compareAndSet(false, true)){
            hashGenerator.getHashesAsync(capacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(()-> filling.set(false));
        }
        return hashes.poll();
    }
}
