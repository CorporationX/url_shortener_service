package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.cache.UrlRedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlRedisCache redisCache;

    @Scheduled(cron = "${scheduling.cleaner}")
    @Transactional
    public List<Hash> clean() {
        List<Url> newUrls = urlRepository.getAndDeleteYearOldObjects();
        redisCache.deleteAll(newUrls.stream().map(Url::getHash).toList());
        List<Hash> newHashes = newUrls.stream().map(a -> new Hash(a.getHash())).toList();
        return hashRepository.saveAll(newHashes);
    }
}