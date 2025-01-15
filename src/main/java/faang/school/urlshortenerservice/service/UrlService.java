package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${urlShortener.domain}")
    private String domain;

    @Value("${urlShortener.protocol}")
    private String protocol;

    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    @Transactional
    public UrlDto shortenUrl(UrlDto dto) {
        String hash = hashCache.getHash();
        Url url = new Url(hash, dto.getUrl());
        urlRepository.save(url);
        urlCacheRepository.saveToCache(url);
        String urlShort = protocol + "://" + domain + "/" + hash;
        return new UrlDto(urlShort);
    }

    public String getUrl(String hash) {
        return urlCacheRepository.getUrlByHash(hash);
    }
}
