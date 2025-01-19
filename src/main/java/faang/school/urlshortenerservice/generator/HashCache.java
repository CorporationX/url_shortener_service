package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.ExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final LocalCache localCache;
    private final ExecutorService executor;

    public String getHash() {
        if (!localCache.hashSizeValidate()) {
            hashGenerator.generateBatch();
            executor.saveHashInCache();
        }
        return localCache.getHash();
    }
}
