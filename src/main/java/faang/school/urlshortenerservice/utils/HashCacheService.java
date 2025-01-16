package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashCacheService {

    private final HashRepository hashRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final HashGenerator hashGenerator;

    @Value("${hash.generator.range-size}")
    private int rangeSize;

    private final int HASH_BELOW_TWENTY_PERCENT = (int) (rangeSize * 0.2);

    @Transactional
    public List<String> fetchAndDeleteHashesFromDb(int count) {
        List<Hash> hashes = hashRepository.findFirstHashes(count);
        List<Long> ids = hashes.stream().map(Hash::getId).collect(Collectors.toList());

        hashRepository.deleteHashesByIds(ids);

        return hashes.stream().map(Hash::getHash).collect(Collectors.toList());
    }

    public void cacheHashes(List<String> hashes) {
        redisTemplate.opsForList().rightPushAll("hashes", hashes);
    }

    public String getHash() {
        Long remainingHashes = redisTemplate.opsForList().size("hashes");

        if (remainingHashes == null || remainingHashes < HASH_BELOW_TWENTY_PERCENT) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                hashGenerator.generateAndSaveHashes(rangeSize);
                List<String> newHashesForCache = fetchAndDeleteHashesFromDb(rangeSize);
                cacheHashes(newHashesForCache);
            });
            executor.shutdown();
        }
        String hash = redisTemplate.opsForList().leftPop("hashes");

        return hash;
    }
}
