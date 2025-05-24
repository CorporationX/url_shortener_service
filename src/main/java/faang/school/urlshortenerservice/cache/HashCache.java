package faang.school.urlshortenerservice.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
public class HashCache {

    private final Queue<String> cache = new ConcurrentLinkedQueue<>();

    public String poll() {
        return cache.poll();
    }

    public void addAll(List<String> hashes) {
        cache.addAll(hashes);
        log.info("Hash cache replenished with {} hashes", hashes.size());
    }

    public int size() {
        return cache.size();
    }
}