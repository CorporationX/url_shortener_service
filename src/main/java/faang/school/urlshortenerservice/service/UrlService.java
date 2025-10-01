package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.HashCreatedException;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public String addLink(String url) {
        Url findUrl = urlRepository.findByUrl(url);
        if (findUrl == null) {
            String hash = hashCache.get();
            if(hash == null) {
                throw new HashCreatedException("Непредвиденная ошибка во время получения хеша из локального хранилища");
            }
            Url newUrl = new Url(hashCache.get(), url, LocalDateTime.now());
            urlRepository.save(newUrl);
            putInRedis(hash, url);
            return newUrl.getHash();
        }
        putInRedis(findUrl.getHash(), url);
        return findUrl.getHash();
    }

    public String findUrl(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        if (url == null) {
            Url findUrl = urlRepository.findByHash(hash);
            return findUrl.getUrl();
        }
        return url;
    }

    private void putInRedis(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }
}
