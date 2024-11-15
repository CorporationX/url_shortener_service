package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCacheFiller {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final HashProperties hashProperties;
    private final HashCache hashCache;

    @PostConstruct
    public void init() {
        hashGenerator.generate();
        int initBatchSize = hashProperties.getCacheCapacity();
        List<String> hashes = hashRepository.getHashBatch(initBatchSize);
        hashCache.setHashBatch(hashes);
        log.info("Hash cache initialized. Filling size: {}", hashes.size());
    }

    @Async("threadPool")
    public void fillCache() {
        hashGenerator.generate();
        int batchSize = hashProperties.getHashBatchSize();
        List<String> hashes = hashRepository.getHashBatch(batchSize);
        hashCache.setHashBatch(hashes);
        log.info("Hash cache filling. Filling size: {}", hashes.size());
    }
}
