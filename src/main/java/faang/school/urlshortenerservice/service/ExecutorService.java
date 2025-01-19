package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExecutorService {
    private final HashRepository hashRepository;
    private final LocalCache localCache;

    @Value("${spring.url-shortener.hash.batch.size}")
    private int batchSize;

    public void saveHashInCache() {
       List<Hash> hashesFromBd = hashRepository.getHashBatch(batchSize);
        localCache.saveHashes(hashesFromBd.stream().map(Hash::getHash).toList());
    }
}
