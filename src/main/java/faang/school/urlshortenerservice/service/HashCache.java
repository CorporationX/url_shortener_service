package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.properties.HashProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
@Data
@RequiredArgsConstructor
public class HashCache {
    private final ThreadPoolTaskExecutor hashCacheExecutor;
    private final ConcurrentSkipListMap<String, String> urlMap = new ConcurrentSkipListMap<>();
    private final HashProperties hashProperties;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    private final AtomicBoolean hashesLoading = new AtomicBoolean(false);
    private Integer cacheSize;
    private Integer thresholdSize;

    @PostConstruct
    public void init() {
        cacheSize = hashProperties.getMaxSize();
        thresholdSize = (cacheSize * hashProperties.getPercentThreshold() / 100);
        loadInitialHashes();
    }


    public String getRandomShortUrl() {
        if (urlMap.isEmpty()) {
            return null;
        }
        List<String> keys = urlMap.keySet().stream().toList();
        Random random = new Random();
        return keys.get(random.nextInt(keys.size()));
    }

    public String getHash() {
        if (urlMap.size() > thresholdSize) {
            return urlMap.firstEntry().getValue();
        }
        if (urlMap.size() < thresholdSize) {
            if (hashesLoading.compareAndSet(false, true)) {
                hashCacheExecutor.execute(() -> {
                    try {
                        List<Hash> hashes = hashRepository.getHashBatch(hashProperties.getBatchSize());
                        Map<String, String> hashesMap = hashes.stream()
                                .collect(Collectors.toMap(
                                        Hash::getHash,
                                        hash -> hash.getUrl().getUrl()
                                ));
                        urlMap.putAll(hashesMap);
                        hashGenerator.generateBatch();
                    } finally {
                        hashesLoading.set(false);
                    }
                });
            }
        }
        throw new IllegalArgumentException("Hash is not found");
    }

    private void loadInitialHashes() {
        if (urlMap.isEmpty() && hashesLoading.compareAndSet(false, true)) {
            hashCacheExecutor.execute(() -> {
                try {
                    List<Hash> hashes = hashRepository.getHashBatch(hashProperties.getBatchSize());
                    Map<String, String> hashesMap = hashes.stream()
                            .collect(Collectors.toMap(
                                    Hash::getHash,
                                    hash -> hash.getUrl().getUrl()
                            ));
                    urlMap.putAll(hashesMap);
                    hashGenerator.generateBatch();
                } finally {
                    hashesLoading.set(false);
                }
            });
        }
    }
}
