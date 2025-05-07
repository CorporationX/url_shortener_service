package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.RedisCashUrl;
import faang.school.urlshortenerservice.entity.RedisUrl;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.RedisUrlRepository;
import faang.school.urlshortenerservice.repository.RedisUrlRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public RedirectView getRedirectUrl  (String hash) {
        //search for url in Redis

        //search for url in PostgreSQL Data Base
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

    public RedisUrl saveCashUrlV2(RedisUrl redisUrl) {
        System.out.println("saveCashUrl working");
        return redisRepositoryV2.save(redisUrl);
    }

    public RedisUrl getCashUrlV2(String hash) {
//        return redisRepositoryV2.findByHash(hash);
        Optional<RedisUrl> optional = redisRepositoryV2.findById(hash);
        return optional.orElseGet(RedisUrl::new);
//        return redisRepositoryV2.findByHash(hash);
    }

    public ArrayList<RedisUrl> getCashUrlAllV2() {
        Iterable<RedisUrl> iterable = redisRepositoryV2.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
