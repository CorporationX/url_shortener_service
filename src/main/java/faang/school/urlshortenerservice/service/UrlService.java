package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.url.SaveUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.localcache.LocalHashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlServiceValidator;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final LocalHashCache localHashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;
    private final UrlServiceValidator urlServiceValidator;

    @Value("${hash.redirect_suffix}")
    private String redirectSuffix;
    @Value("${app_link}")
    private String appLink;
    @Value("${server.port}")
    private int serverPort;
    private String urlShortenerServiceLink;

    @PostConstruct
    public void init() {
        urlShortenerServiceLink = appLink + ":" + serverPort + redirectSuffix;
    }

    public String getShortUrl(SaveUrlDto saveUrlDto) {
        log.info("get cached hash from local cash");
        String hash = localHashCache.getCachedHash();
        Url url = urlMapper.toEntity(saveUrlDto);
        url.setHash(hash);

        log.info("get url from DB with hash {}", hash);
        Url savedUrl = urlRepository.save(url);

        cacheUrl(savedUrl);

        return urlShortenerServiceLink + savedUrl.getHash();
    }

    public String getUrl(String hash) {
        log.info("validate hash");
        urlServiceValidator.validateHash(hash);

        log.info("get url with hash {} from local cash", hash);
        String cachedUrl = urlCacheRepository.getUrl(hash);
        if (cachedUrl != null) {
            log.info("cached url {}", cachedUrl);
            return cachedUrl;
        }

        log.info("get url with hash {} from DB", hash);
        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url mapping by hash %s does not exist".formatted(hash)));

        cacheUrl(url);

        return url.getUrl();
    }

    public void cacheUrl(Url url) {
        log.info("cache {}", url);
        urlCacheRepository.cacheUrl(url.getHash(), url.getUrl());
    }
}
