package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.redis.RedisConfig;
import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlHashCacheService {
    private static final String KEY = "hash_url_map";
    private final RedisConfig redisConfig;
    private final UrlService urlService;

    public RedissonClient connectionToRedis() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisConfig.getHost() + ":" + redisConfig.getPort());

        return Redisson.create(config);
    }

    public void cacheHashUrl(String hash, String url) {
        RedissonClient redisson = connectionToRedis();
        RMap<String, String> hashUrlMap = redisson.getMap(KEY);
        hashUrlMap.put(hash, url);
    }

    public String getUrlByRedis(String hash) {
        RedissonClient redisson = connectionToRedis();
        RMap<String, String> hashUrlMap = redisson.getMap(KEY);
        String url = hashUrlMap.get(hash);
        if (url == null) {
            throw new NoSuchElementException("Url Nof Found");
        }
        return url;
    }

    public String getUrlByHash(String hash) {

        RedissonClient redisson = connectionToRedis();
        RMap<String, String> hashUrlMap = redisson.getMap(KEY);
        String url = hashUrlMap.get(hash);
        if (url != null) {
            return url;
        }
        Optional<Url> urlFromDb = urlService.findUrlByHash(hash);
        if(urlFromDb.isPresent()) {
            cacheHashUrl(hash, urlFromDb.get().getUrl());
            return urlFromDb.get().getUrl();
        } else{
            throw new NoSuchElementException();
        }
    }
}
