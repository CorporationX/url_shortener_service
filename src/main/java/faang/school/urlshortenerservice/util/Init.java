package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.properties.HashCacheQueueProperties;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import faang.school.urlshortenerservice.service.hash_cache.HashCache;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class Init {

    private final HashCache hashCache;
    private final HashGenerator hashGenerator;
    private final HashCacheQueueProperties properties;

    @PostConstruct
    @Transactional
    public void init() {
        hashGenerator.generateBatchHashes(properties.getMaxQueueSize())
                .thenRun(hashCache::fillCache);
    }
}
