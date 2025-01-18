package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.service.ExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final UrlCacheRepository urlCacheRepository;
    private final ExecutorService executor;

    public Hash getHash() {
        hashGenerator.generateBatch();

        Hash hash = urlCacheRepository.getHashInCache();

        if (urlCacheRepository.hashSizeValidate()) {
            return hash;
        } else {
            executor.saveHashInCache();
            return hash;
        }
    }
}
