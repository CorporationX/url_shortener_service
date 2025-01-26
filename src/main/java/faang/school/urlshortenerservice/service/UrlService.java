package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final HashService hashService;
    private final UrlCacheRepository urlCacheRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${api.base-url}")
    private String baseUrl;

    public UrlDto shortenUrl(UrlDto urlDto) {
        log.info("Shortening the URL: {}", urlDto.url());
        String hash = hashCache.getHash();
        Url url = new Url(urlDto.url(), hash);

        log.info("Hash before save: {}", url.getHash());
        urlRepository.save(url);
        urlCacheRepository.saveToCache(hash, urlDto.url());

        return UrlDto.builder()
                .url(baseUrl + "/" + hash)
                .build();
    }

    @Cacheable(value = "url", key = "#hash")
    public String getOriginalUrl(String hash) {
        log.info("Getting the original URL from the database: {}", hash);

        return urlRepository.findByHash(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new IllegalArgumentException("URL not found"));

    }

    @Transactional
    public void deleteOldRecordsAndSaveHashes() {
        log.info("Deleting old records from the database");
        List<Hash> hashes = urlRepository.deleteOldRecordsAndReturnHashes().stream()
                .map(Hash::new)
                .toList();
        hashService.saveHashes(hashes);
        log.info("{} outdated URLs have been deleted", hashes.size());
    }

}
