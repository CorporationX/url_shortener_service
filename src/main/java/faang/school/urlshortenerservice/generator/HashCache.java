package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.cache_size:1000}") // Значение по умолчанию: 1000
    private int cacheSize;

    private final HashGenerator hashGenerator;
    private BlockingQueue<String> hashQueue;

    private AtomicBoolean isRunNewGenerator = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        this.hashQueue = new ArrayBlockingQueue<>(cacheSize);
    }

    public String getHash(){
        if ((hashQueue.size() * 100) / cacheSize < 20 ){
            if (!isRunNewGenerator.compareAndExchange(false, true)) {
                hashGenerator.generateBatch();
                isRunNewGenerator.set(true);
            }
        }
        try {
            return hashQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
