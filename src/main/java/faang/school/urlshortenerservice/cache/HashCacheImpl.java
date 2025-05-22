package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.HashCacheProperties;
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
    private final HashCacheProperties properties;

    private final Queue<String> hashQueue = new ConcurrentLinkedQueue<>();

    @PostConstruct
    private void init() {
        fillCache();
    }

    @Override
    public String getHash() {
        if (hashQueue.size() < properties.getMinThreshold()) {
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
        List<HashEntity> hashes = hashRepository.findAllBy(PageRequest.of(0, properties.getBatchSize()));
        hashes.forEach(h -> hashQueue.add(h.getHash()));
        log.info("Loaded {} hashes into cache", hashes.size());
    }
}
