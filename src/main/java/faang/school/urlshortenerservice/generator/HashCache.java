package faang.school.urlshortenerservice.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.cache_size:1000}") // Значение по умолчанию: 1000
    private int cacheSize;

    private final BlockingQueue<String> hashQueue;

    public HashCache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.hashQueue  = new ArrayBlockingQueue<>(cacheSize);
    }

    public String getHash(){
        if ((hashQueue.size() * 100) / cacheSize < 20 ){
//            вызываем
        }
        try {
            return hashQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
