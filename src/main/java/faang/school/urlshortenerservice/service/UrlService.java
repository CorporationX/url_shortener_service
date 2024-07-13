package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public String createShortUrl(String url) {
        String hash = hashCache.getHash();
        urlRepository.save(hash, url);
        redisTemplate.opsForValue().set(hash, url);
        return hash;
    }
}
