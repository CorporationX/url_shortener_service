package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.model.dto.url.UrlDto;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository cacheRepository;

    @Value("${short-url.format:http://short.url/%s}")
    private String format;

    @Transactional
    @Override
    public String createShortUrl(UrlDto dto) {
        Url url = new Url();
        url.setHash(hashCache.getHash().getHash());
        url.setUrl(dto.url());

        Url saveUrl = urlRepository.saveAndFlush(url);
        log.info("The URL is saved in the database: {}", saveUrl);

        cacheRepository.saveHash(saveUrl);
        log.info("The URL is cached in Redis.Hash: {}, url: {}", saveUrl.getHash(), saveUrl.getUrl());

        return String.format(format, saveUrl.getHash());
    }

    @Override
    @Transactional(readOnly = true)
    public String getOriginalUrl(UrlDto urlDto) {
        int lastSlashIndex = urlDto.url().lastIndexOf('/');
        String shortUrl = urlDto.url().substring(lastSlashIndex + 1);
        String originalUrl = cacheRepository.getUrl(shortUrl);

        if (originalUrl == null) {
            Url url = urlRepository.findByHash(shortUrl).
                    orElseThrow(() -> new EntityNotFoundException("Not found url: " + urlDto.url()));
            originalUrl = url.getUrl();

            cacheRepository.saveHash(url);
            log.info("The received url from the database is saved in Redis.Hash: {}, url: {}",
                    url.getHash(), url.getUrl());
        }
        return originalUrl;
    }
}
