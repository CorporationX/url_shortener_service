package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlAsyncService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final HashCacheService hashCacheService;
    @Value("${spring.hash.fetch-size:1000}")
    private int fetchSize;
    private final double hashBatchToFetchRatio = 3.0;

    @Async("hashGeneratorExecutor")
    public void saveShortAndLongUrlToDataBase(String hash, String url) {
        log.info("Save start for hash to SQL DB: {}; Thread - {}", hash, Thread.currentThread().getName());
        urlRepository.save(hash, url);
        log.info("Save hash and url to SQL DB: {} , {}", hash, url);
    }


    @Async("hashGeneratorExecutor")
    public CompletableFuture<Void> importShortUrlHashesToQueueCash() {
        try {
            List<String> hashes = hashRepository.getHashBatch();
            double hashSize = (double) hashRepository.getHashSize();
            if (hashSize / fetchSize < hashBatchToFetchRatio) {
                hashGenerator.generateBatch();
            }
            hashCacheService.addHashesToQueue(hashes);
            log.info("importShortUrlHashesToQueueCash: hashes taken from DB: {}, {}", hashes, Thread.currentThread().getName());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error in importShortUrlHashesToQueueCash", e);
            return CompletableFuture.failedFuture(e);
        }




    }
}
