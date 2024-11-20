package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.cache.CacheProperties;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final CacheProperties cacheProperties;
    private final RedisTemplate<String, Url> redisTemplate;

    @Transactional
    public void releaseExpiredHashes() {
        log.info("start moderateDB");

        List<String> existingHashes = urlRepository.getHashesAndDeleteExpiredUrls(cacheProperties.getNonWorkingUrlTime());
        log.info("get {} existingHashes", existingHashes.size());

        hashRepository.saveAllHashesBatched(existingHashes.stream()
                .map(Hash::new)
                .toList());

        log.info("finish moderateDB");
    }

    @Transactional
    public UrlDto createShortUrl(UrlDto urlDto) {
        log.info("start createShortUrl with urlDto: {}", urlDto);

        String hash = hashCache.getHash();
        log.info("get hash: {}", hash);

        Url url = urlRepository.findUrlByHash(hash)
                .orElse(urlRepository.save(urlMapper.toEntity(urlDto, hash)));
        log.info("get Url: {}", url);

        redisTemplate.opsForValue().set(url.getHash(), url);
        log.info("save Url in Redis cache: {}", redisTemplate.opsForValue().get(url.getHash()));

        UrlDto shortUrlDto = urlMapper.toDto(url);
        log.info("finish createShortUrl with shortUrl: {}", shortUrlDto);
        return shortUrlDto;
    }
}
