package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.RedisService;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalCache {
    private final RedisService redisService;
    private final UrlRepository urlRepository;

    public <T> void save(String key, T value) {
        redisService.save(key, value);
    }

//    public String getOriginalUrl(String key) {
//
//        String originalUrl = redisService.get(key, String.class)
//                .orElseGet(() -> {
//                    var urlEntity = urlRepository.findByHash(key);
//                    if (urlEntity != null) {
//                        return urlEntity.getOriginalUrl();
//                    }
//                    throw new EntityNotFoundException(String.format("Resource with key = %s not found", key));
//                });
//
//        // Save to local cache if the original URL exists
//        //saveToLocalCache(key, originalUrl);
//
//        return originalUrl;
//
//    }

    public void delete(String key) {
        // delete from local cache
    }
}
