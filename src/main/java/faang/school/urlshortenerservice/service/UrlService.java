package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.RedisCashUrl;
import faang.school.urlshortenerservice.entity.RedisUrl;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.RedisUrlRepository;
import faang.school.urlshortenerservice.repository.RedisUrlRepositoryV2;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.HTTP;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashGenerator hashGenerator;
    private final RedisUrlRepository redisRepository;
    private final RedisUrlRepositoryV2 redisRepositoryV2;
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String shortUrlQueueName = "ShortUrlList";

    public RedirectView getRedirectUrl(String hash) {
        //search for url in Redis
        Optional<RedisUrl> optionalRedisUrl = redisRepositoryV2.findById(hash);
        RedirectView redirectView = new RedirectView();
        log.info("redisUrl = {}", optionalRedisUrl);
        if(optionalRedisUrl.isPresent()) {
            log.info("Hash {} is found in cash", hash);
            redirectView.setUrl(optionalRedisUrl.get().getUrl() + "/bingo");
            redirectView.setStatusCode(HttpStatusCode.valueOf(302));
           return redirectView;
        } else {
            log.info("Hash {} is not found in cash. Start Searching in DB", hash);
            String longUrl = urlRepository.find(hash);
            log.info("longUrl = {} Searched in SQL DB", longUrl);
            if(longUrl!=null) {
                redirectView.setUrl(longUrl + "/bingo");
                redirectView.setStatusCode(HttpStatusCode.valueOf(302));
                return redirectView;
            }
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

    public List<String> importShortUrlHashesToCash(){
        List<String> hashes = hashRepository.getHashBatch();
        log.info("importShortUrlHashesToCash: hashes taken from DB: {}", hashes);
        for(String hash : hashes) {
            redisTemplate.opsForList().leftPush(shortUrlQueueName, hash);
        }
        return hashes;
    }

    public String popShortUrl() {
        return (String) redisTemplate.opsForList().rightPop(shortUrlQueueName);
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

    public RedisUrl saveCashUrlV2(String url) {
        RedisUrl redisUrl = new RedisUrl();
        List<Long> shortUrl = new ArrayList<>();
        redisUrl.setUrl(url);
        redisUrl.setHash(popShortUrl());
        System.out.println("saveCashUrl working");
        saveShortAndLongUrlToDataBase(redisUrl.getHash(), redisUrl.getUrl());
        return redisRepositoryV2.save(redisUrl);
    }

    public void saveShortAndLongUrlToDataBase(String hash, String url) {
        urlRepository.save(hash, url);
    }

    public RedisUrl getCashUrlV2(String hash) {
        Optional<RedisUrl> optional = redisRepositoryV2.findById(hash);
        if(optional.isPresent()) {
            log.info("Optional<RedisUrl> optional = {}", optional);
        }
        return optional.orElseGet(RedisUrl::new);
    }

    public ArrayList<RedisUrl> getCashUrlAllV2() {
        Iterable<RedisUrl> iterable = redisRepositoryV2.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
