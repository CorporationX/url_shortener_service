package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.async.AsyncConfig;
import faang.school.urlshortenerservice.entity.RedisUrl;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
@Getter
public class UrlService {
    private final HashGenerator hashGenerator;
    private final double hashBatchToFetchRatio = 3.0;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String shortUrlQueueName = "ShortUrlList";
    @Value("${spring.hash.fetch-size:1000}")
    private int fetchSize;
    //    private final ArrayBlockingQueue<String> hashCashQueue;
    private final AsyncConfig asyncConfig;
    private final UrlAsyncService urlAsyncService;
    private final HashCacheService hashCacheService;
    private Integer queueSizeLock;
    private final AtomicBoolean isImportRunning = new AtomicBoolean(false);

    public RedisUrl setShortUrl(String url) {
        RedisUrl redisUrl = new RedisUrl();
        redisUrl.setUrl(url);
        redisUrl.setHash(popShortUrlFromQueue());
        log.info("RedisUrl save to Redis start");
        redisUrl = urlCacheRepository.save(redisUrl);
        log.info("RedisUrl is saved to Redis: {}, {}", redisUrl.getHash(), url);
        urlAsyncService.saveShortAndLongUrlToDataBase(redisUrl.getHash(), redisUrl.getUrl());
        return redisUrl;
    }

    public RedirectView getRedirectUrl(String hash) {
        //search for url in Redis
        log.info("urlCacheRepository - start search of {}", hash);
        Optional<RedisUrl> optionalRedisUrl = urlCacheRepository.findById(hash);
        log.info("urlCacheRepository - end search: redisUrl = {} for {}", optionalRedisUrl, hash);
        RedirectView redirectView = new RedirectView();
        if (optionalRedisUrl.isPresent()) {
            redirectView.setUrl(optionalRedisUrl.get().getUrl() + "/bingo");
            redirectView.setStatusCode(HttpStatusCode.valueOf(200));
            return redirectView;
        } else {
            log.info("urlRepository - start search of {}", hash);
            String longUrl = urlRepository.find(hash);
            log.info("urlRepository - end search: DbUrl = {} for {}", longUrl, hash);
            if (longUrl != null) {
                redirectView.setUrl(longUrl + "/bingo");
                redirectView.setStatusCode(HttpStatusCode.valueOf(200));
                return redirectView;
            }
            return new RedirectView();
        }
    }

    public RedirectView getRedirectUrlFromSQLDb(String hash) {
        RedirectView redirectView = new RedirectView();
        log.info("getRedirectUrlFromSQLDb: urlRepository - start search of {}", hash);
        String longUrl = urlRepository.find(hash);
        log.info("getRedirectUrlFromSQLDb: urlRepository - end search: DbUrl = {} for {}", longUrl, hash);
        if (longUrl != null) {
            redirectView.setUrl(longUrl + "/bingo");
            redirectView.setStatusCode(HttpStatusCode.valueOf(200));
            return redirectView;
        }
        return new RedirectView();
    }

    public void generateHashes() {
        // Вызов асинхронного метода
        CompletableFuture<Void> future = hashGenerator.generateBatch();

        // Обработка результата (опционально)
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                System.err.println("Error in hash generation: " + ex.getMessage());
            } else {
                System.out.println("Successful hash generation");
            }
        });
    }

    @Async("hashGeneratorExecutor")
    public List<String> importShortUrlHashesToQueueCash() {
        List<String> hashes = hashRepository.getHashBatch();
        double hashSize = (double) hashRepository.getHashSize();
        if (hashSize / fetchSize < hashBatchToFetchRatio) {
            hashGenerator.generateBatch();
        }
        log.info("importShortUrlHashesToQueueCash: hashes taken from DB: {}, {}", hashes, Thread.currentThread().getName());
//        hashCashQueue.addAll(hashes);
        hashCacheService.addHashesToQueue(hashes);
        return hashes;
    }

    @PostConstruct
    public List<String> importShortUrlHashesToQueueCashStart() {
        List<String> hashes = hashRepository.getHashBatch();
        double hashSize = (double) hashRepository.getHashSize();
        if (hashSize / fetchSize < hashBatchToFetchRatio) {
            hashGenerator.generateBatch();
        }
//        log.info("importShortUrlHashesToQueueCashStart: hashes taken from DB: {}", hashes);
//        hashCashQueue.addAll(hashes);
        hashCacheService.addHashesToQueue(hashes);
        return hashes;
    }

    public String popShortUrlFromQueue() {
        queueSizeLock = hashCacheService.getQueueSize();
        double cashPercentLevel = ((double) (queueSizeLock) / fetchSize * 100);
//        log.info(">       isImportRunning.get() = {}", isImportRunning.get());
        if (cashPercentLevel <= 20) {
            if(isImportRunning.compareAndSet(false, true)) {
                try {
                    isImportRunning.set(true);
//                    log.info(">>>>> lock1.set(true)");
//                    log.info(">>> Autostart of importShortUrlHashesToQueueCash because cash size less than 20 percent");
                    urlAsyncService.importShortUrlHashesToQueueCash()
                            .whenComplete((result, ex) -> {
                                        isImportRunning.set(false);
                                        if (ex != null) {
                                            log.error("Error in importShortUrlHashesToQueueCash", ex);
                                        }
                                    }

                            );
//                    log.info(">>>>> lock1.set(false)");

                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return hashCacheService.popHash();
    }

    public String popShortUrlFromDB() {
        List<String> hashes = hashRepository.getHashBatch();
        return hashes.get(0);
    }

    public RedisUrl getCashUrl(String hash) {
        Optional<RedisUrl> optional = urlCacheRepository.findById(hash);
        if (optional.isPresent()) {
            log.info("Optional<RedisUrl> optional = {}", optional);
        }
        return optional.orElseGet(RedisUrl::new);
    }

    public ArrayList<RedisUrl> getCashUrlAllV2() {
        Iterable<RedisUrl> iterable = urlCacheRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<String> getHashesFromUrlTable(int number) {
        return urlRepository.findTop(number);
    }

    public long getCashQueueSize() {
//        return hashCashQueue.size();
        return hashCacheService.getQueueSize();
    }
}
