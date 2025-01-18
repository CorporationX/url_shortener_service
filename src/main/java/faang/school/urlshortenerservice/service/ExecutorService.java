package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExecutorService {
    private final HashRepository hashRepository;
    private final UrlCacheRepository urlCacheRepository;

    public void saveHashInCache() {
       List<Hash> hashesFromBd = hashRepository.getHashBatch(20);
       urlCacheRepository.saveHashInCache(hashesFromBd);
    }
}
