package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.RedisCache;
import faang.school.urlshortenerservice.config.properties.HashProperties;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exeption.url.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final RedisCache redisCache;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private HashCache hashCache;
    private final HashProperties hashProperties;
    private UrlMapper urlMapper;

    @Override
    public Url getOriginalUrl(String hash) {
        String longUrl = String.valueOf(redisCache.getFromCache(hash));
        if (longUrl != null) {
            return new Url(hash, longUrl);
        }

        Url url = urlRepository.getUrlByHash(hash);
        if (url == null) {
            log.error("URL not found for hash: {}", hash);
            throw new UrlNotFoundException("URL not found for hash: " + hash);
        }

        redisCache.saveToCache(url.getHash(), url.getUrl());
        return url;
    }
    @Override
    @Transactional
    public UrlDto convertLongUrl(Url longUrl) {
        longUrl.setHash(hashCache.getHash());
        redisCache.saveToCache(longUrl.getHash(), longUrl.getUrl());
        urlRepository.save(longUrl);
        return urlMapper.toDto(longUrl);
    }
    @Override
    @Transactional
    public List<String> cleanOldUrls() {
        List<String> deletedHashes = urlRepository.deleteOldUrlsAndReturnHashes(hashProperties.getInterval());
        log.info("Удалены хэши старых URL: {}", deletedHashes);

        for (String hash : deletedHashes) {
            Hash hashEntity = hashRepository.findByHash(hash);
            if (hashEntity != null) {
                hashEntity.setUrl(null);
                hashRepository.save(hashEntity);
            }
        }
        return deletedHashes;
    }
}
