package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.generator.RedisCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;
    private final RedisCache redisCache;

    @Override
    public String getUrlByHash(String hash) {
        return redisCache.getUrlFromCache(hash);

    }

    @Override
    public String getHashByUrl(String url) {
        String hash = hashCache.getHash();

        redisCache.cacheUrl(hash, url);

        urlRepository.save(Url.builder()
                .hash(hash)
                .url(url)
                .createdAt(LocalDateTime.now())
                .build());
        return hash;
    }

    @Override
    public void deleteOldHashes() {
        LocalDateTime date = LocalDateTime.now().minusYears(1);
        hashRepository.saveAll(urlRepository.deleteOldUrls(date));
    }

}
