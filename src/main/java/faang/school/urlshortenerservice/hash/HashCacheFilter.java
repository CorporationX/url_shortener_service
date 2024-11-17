package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCacheFilter {

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final HashCache hashCache;
    private final HashProperties hashProperties;

    @PostConstruct
    public void init() {
        hashGenerator.generateHashes();
        int initBatchSize = hashProperties.getCacheCapacity();
        List<String> hashes = hashRepository.getHashBatch(initBatchSize);
        hashCache.setHashBatch(hashes);
        log.info("Hash cache initialized. Filling size: {}", hashes.size());
    }

    @Async("thread-pool")
    public void fillCache() {
        hashGenerator.generateHashes();
        int batchSize = hashProperties.getHasBatchSize();
        List<String> hashes = hashRepository.getHashBatch(batchSize);
        hashCache.setHashBatch(hashes);
        log.info("Hash cache filled. Filling size: {}", hashes.size());
    }
}
