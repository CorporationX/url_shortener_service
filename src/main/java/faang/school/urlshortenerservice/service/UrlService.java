package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.RedisCashUrl;
import faang.school.urlshortenerservice.entity.RedisUrl;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.RedisUrlRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashGenerator hashGenerator;
    private final double hashBatchToFetchRatio = 3.0;
    private final RedisUrlRepository redisRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String shortUrlQueueName = "ShortUrlList";
    @Value("${spring.hash.fetch-size:1000}")
    private int fetchSize;
    private final ArrayBlockingQueue<String> hashCashQueue;

    public RedirectView getRedirectUrl(String hash) {
        //search for url in Redis
        log.info("urlCacheRepository - start search of {}", hash);
        Optional<RedisUrl> optionalRedisUrl = urlCacheRepository.findById(hash);
        log.info("urlCacheRepository - end search: redisUrl = {} for {}", optionalRedisUrl, hash);
        RedirectView redirectView = new RedirectView();
        if(optionalRedisUrl.isPresent()) {
            redirectView.setUrl(optionalRedisUrl.get().getUrl() +  "/bingo");
            redirectView.setStatusCode(HttpStatusCode.valueOf(200));
           return redirectView;
        } else {
            log.info("urlRepository - start search of {}", hash);
            String longUrl = urlRepository.find(hash);
            log.info("urlRepository - end search: DbUrl = {} for {}", longUrl, hash);
            if(longUrl!=null) {
                redirectView.setUrl(longUrl + "/bingo");
                redirectView.setStatusCode(HttpStatusCode.valueOf(200));
                return redirectView;
            }
        }
        return new RedirectView();
    }

    public RedirectView getRedirectUrlFromSQLDb(String hash) {
            RedirectView redirectView = new RedirectView();
            log.info("getRedirectUrlFromSQLDb: urlRepository - start search of {}", hash);
            String longUrl = urlRepository.find(hash);
            log.info("getRedirectUrlFromSQLDb: urlRepository - end search: DbUrl = {} for {}", longUrl, hash);
            if(longUrl!=null) {
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

    @Async
    public List<String> importShortUrlHashesToQueueCash() {
        List<String> hashes = hashRepository.getHashBatch();
        double hashSize = (double)hashRepository.getHashSize();
        if(hashSize/fetchSize < hashBatchToFetchRatio) {
            hashGenerator.generateBatch();
        }
        log.info("importShortUrlHashesToQueueCash: hashes taken from DB: {}", hashes);
        hashCashQueue.addAll(hashes);
        return hashes;
    }

    @PostConstruct
    public List<String> importShortUrlHashesToQueueCashStart() {
        List<String> hashes = hashRepository.getHashBatch();
        double hashSize = (double)hashRepository.getHashSize();
        if(hashSize/fetchSize < hashBatchToFetchRatio) {
            hashGenerator.generateBatch();
        }
        log.info("importShortUrlHashesToQueueCashStart: hashes taken from DB: {}", hashes);
        hashCashQueue.addAll(hashes);
        return hashes;
    }

    public String popShortUrlFromQueue() {
        int queueSize = hashCashQueue.size();
        double cashPercentLevel = ((double)(queueSize)/fetchSize*100);
        if(cashPercentLevel <= 20) {
            log.info(">>> Autostart of importShortUrlHashesToQueueCash because cash size less than 20 percent");
            importShortUrlHashesToQueueCash();
        }
        return hashCashQueue.poll();
    }

    public String popShortUrlFromDB() {
        List<String> hashes = hashRepository.getHashBatch();
        return hashes.get(0);
    }

    public RedisCashUrl getUrlFromCash(String hash) {
        log.info("Searching for hash in Redis: {}" ,hash);
        RedisCashUrl result = redisRepository.findByHash(hash);
        log.info("Redis returned: {}" ,result);
        return redisRepository.findByHash(hash);

    }

    public RedisCashUrl saveCashUrl(RedisCashUrl redisCashUrl) {
        System.out.println("saveCashUrl working");
        return redisRepository.save(redisCashUrl);
    }

    public RedisUrl setShortUrl(String url) {
        RedisUrl redisUrl = new RedisUrl();
        redisUrl.setUrl(url);
        redisUrl.setHash(popShortUrlFromQueue());
        log.info("ShortUrl {} is set for URL {}", redisUrl.getHash(), url);
        saveShortAndLongUrlToDataBase(redisUrl.getHash(), redisUrl.getUrl());
        return urlCacheRepository.save(redisUrl);
    }

    @Async
    public void saveShortAndLongUrlToDataBase(String hash, String url) {
        urlRepository.save(hash, url);
    }

    public RedisUrl getCashUrlV2(String hash) {
        Optional<RedisUrl> optional = urlCacheRepository.findById(hash);
        if(optional.isPresent()) {
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
        return hashCashQueue.size();
    }
}
