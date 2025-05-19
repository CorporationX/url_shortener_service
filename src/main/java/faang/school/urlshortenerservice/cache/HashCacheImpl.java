package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCacheImpl implements HashCache {

    private final HashRepository hashRepository;
    private static final int BATCH_SIZE = 100;
    private static final int MIN_THRESHOLD = 10;

    private final Queue<String> hashQueue = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void init() {
        fillCache();
    }

    @Override
    public String getHash() {
        if (hashQueue.size() < MIN_THRESHOLD) {
            fillCache();
        }
        String hash = hashQueue.poll();
        if (hash != null) {
            hashRepository.deleteById(hash);
        } else {
            throw new IllegalStateException("No available hashes in cache or DB");
        }
        return hash;
    }

    private void fillCache() {
        List<HashEntity> hashes = hashRepository.findAllBy(PageRequest.of(0, BATCH_SIZE));
        hashes.forEach(h -> hashQueue.add(h.getHash()));
        log.info("Loaded {} hashes into cache", hashes.size());
    }
}
