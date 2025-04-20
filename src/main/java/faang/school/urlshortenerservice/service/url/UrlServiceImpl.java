package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;
    private final HashService hashService;
    private final Executor taskExecutor;

    private final AtomicBoolean isLoading = new AtomicBoolean(false);

    @Value("${url.service.hash-size:250}")
    private int hashSize;

    @Value("${url.service.cache-size}")
    private int cacheSize;

    @Value("${url.service.min-available-percentage}")
    private int minPercentageHashes;

    private BlockingQueue<String> hashCache;

    @PostConstruct
    public void populateUrlCache() {
        List<Url> populateUrls = urlRepository.findRecentUrls(cacheSize);
        urlCacheRepository.saveAll(populateUrls);
        hashCache   = new ArrayBlockingQueue<>(hashSize);
        loadHashes(hashSize);
    }

    @Transactional
    @Override
    public Mono<UrlDto> shortenUrl(String originalUrl) {
        Url url = Url.builder()
                .url(originalUrl)
                .hash(getAvailableHash())
                .build();

        return Mono.fromCallable(() -> {
            urlRepository.save(url);
            urlCacheRepository.save(url);
            return urlMapper.toDto(url);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<String> getOriginalUrl(String hash) {
        return urlCacheRepository.get(hash)
                .switchIfEmpty(Mono.just(urlRepository.findById(hash)
                        .orElseThrow(() -> new NoSuchElementException("Url with this hash was not found"))
                        .getUrl()));
    }

    private String getAvailableHash() {
        int actualPercentageHashes = (hashCache.size() * 100) / hashSize;
        if (actualPercentageHashes < minPercentageHashes &&
                isLoading.compareAndSet(false, true)) {
                taskExecutor.execute(() -> loadHashes(hashSize - hashCache.size()));
        }

        return hashCache.poll();
    }

    private void loadHashes(int size) {
        hashService.getHashes(size)
                .doFinally((s) -> isLoading.set(false))
                .doOnError(e -> {
                    log.error("Error occurred: {}", e.getMessage(), e);
                    throw new RuntimeException(e);
                })
                .subscribe(hashes -> hashCache.addAll(hashes));
    }
}