package faang.school.urlshortenerservice.cleaner;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "${cleaner_scheduler.cron}")
    @Async(value = "customPool")
    public void cleaner() {
        List<String> oldUrl = urlRepository.getOldHash();
        List<Hash> hashes = oldUrl.stream()
                .map(hash -> Hash.builder().hash(hash).build())
                .toList();
        hashRepository.saveAll(hashes);

        redisTemplate.delete(oldUrl);
        log.info("_______Hashes have been moved from table url to table hash. Value: {}", oldUrl.size());
    }
}
