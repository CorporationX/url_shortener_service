package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.RedisCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exeption.url.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final RedisCache redisCache;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
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
    public UrlDto convertLongUrl(String longUrl) {
        String hash = hashCache.getHash();
        redisCache.saveToCache(hash, longUrl);
        Url newUrl = new Url(hash, longUrl);
        urlRepository.save(newUrl);
        return urlMapper.toDto(newUrl);
    }
}
