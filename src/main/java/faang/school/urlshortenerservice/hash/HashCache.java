package faang.school.urlshortenerservice.hash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
public class HashCache {
    private final Queue<String> hashCache;

    public HashCache() {
        this.hashCache = new ConcurrentLinkedQueue<>();
    }

    public String getHash() {
        String hash = hashCache.poll();
        log.info("Get hash: {}", hash);
        return hash;
    }

    public void setHashBatch(List<String> hashes) {
        hashCache.addAll(hashes);
        log.info("Set new hashes ({})", hashes.size());
    }

    public int getSize() {
        return hashCache.size();
    }
}

