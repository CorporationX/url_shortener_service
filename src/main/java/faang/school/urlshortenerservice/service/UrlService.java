package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.Url.UrlRepository;
import faang.school.urlshortenerservice.repository.Url.UrlRepositoryJdbc;
import faang.school.urlshortenerservice.service.hash.HashCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class UrlService {

    private static final int BATCH_SIZE = 100;
    private final HashCache cache;
    private final UrlRepository urlRepository;
    private final UrlRepositoryJdbc repositoryJDBC;
    private final ExecutorService saveExecutor = Executors.newSingleThreadExecutor();
    private final Map<String, String> localCache = new ConcurrentHashMap<>();

    @Transactional
    @CachePut(value = "urls", key = "#hash")
    public String shorten(UrlRequestDto requestDto) {
        String hash = cache.getHash();
        String url = requestDto.getUrl();

        localCache.put(hash, url);

        if (localCache.size() >= BATCH_SIZE) {
            flushBatch();
        }

        return hash;
    }

    @Cacheable(value = "urls", key = "#hash")
    public String findUrlByHash(String hash) {
        if (localCache.containsKey(hash)) {
            return localCache.get(hash);
        }
        return urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFoundException(hash));
    }

    @Transactional
    @Scheduled(fixedRate = 5000)
    public synchronized void flushBatch() {
        if (!localCache.isEmpty()) {
            List<Url> urls = localCache.entrySet().stream()
                    .map(entry -> new Url(entry.getKey(), entry.getValue(), LocalDateTime.now()))
                    .toList();

            saveExecutor.submit(() -> repositoryJDBC.saveUrlsBatch(urls));
            localCache.clear();
        }
    }
}