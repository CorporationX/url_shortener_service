package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final BoundHashOperations<String, String, String> urlByHashOps;

    @Async("redisSaveUrlPool")
    public void cacheUrl(String hash, String url) {
        if (hash == null) {
            throw new IllegalArgumentException("hash cannot be null");
        }
        urlByHashOps.put(hash, url);
    }

    public String getUrl(String hash) {
        if (hash == null) {
            throw new IllegalArgumentException("hash cannot be null");
        }

        return urlByHashOps.get(hash);
    }

    public void evictUrl(List<Hash> hashes) {
        if (hashes == null) {
            throw new IllegalArgumentException("list of hashes cannot be null");
        }
        List<String> hashStrings = hashes.stream()
                .map(Hash::getHash)
                .toList();
        urlByHashOps.delete(hashStrings);
    }
}
