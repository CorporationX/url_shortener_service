package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCash;
import faang.school.urlshortenerservice.cache.HashGenerator;
import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final HashCash hashCache;
    private final LocalCache localCache;

    public String shortenUrl(String longUrl) {
        String hash = hashCache.getHash(longUrl);
        if (hash != null) {
            return "http://short.url/" + hash;
        }
        hash = localCache.getHash();
        urlRepository.save(new Url(hash, longUrl));
        hashCache.putHash(longUrl, hash);
        return "http://short.url/" + hash;
    }
}
