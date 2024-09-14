package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.fill.percent}")
    private int fillPercent;

    private final Queue<String> hashes = new ArrayBlockingQueue<>(capacity);

    @PostConstruct
    public void init() {
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (capacity/hashes.size()*100<fillPercent) {
            hashGenerator.getHashesAsync(capacity);
        }
        return hashes.poll();
    }

}
