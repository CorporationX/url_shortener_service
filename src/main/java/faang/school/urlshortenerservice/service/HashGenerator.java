package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.RedisServiceImpl;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final RedisServiceImpl redisService;
    @Value("${services.hash-service.batch-size}")
    private int batchSize;
    @Value("${services.hash-service.hash-cache}")
    private String redisKey;

    @Async("customTaskExecutor")
    public void generateBatchHashes(int batchSize) {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        log.info("Unique numbers retrieved: {}", uniqueNumbers);
        List<Hash> hashList = base62Encoder.encode(uniqueNumbers);
        hashRepository.saveAll(hashList);
        log.info("Successfully saved {} hashes to the repository.", hashList.size());
        redisService.save(redisKey, hashList);
        log.info("Successfully saved {} hashes to the redis.", hashList.size());
    }
}
