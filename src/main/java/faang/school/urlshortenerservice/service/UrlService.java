package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String createShortUrl(UrlDto urlDto) {
        Hash hash = hashCache.getHash();
        Url urlEntity = new Url(hash.getHash(), urlDto.getUrl());
        urlRepository.save(urlEntity);
        urlCacheRepository.save(hash.getHash(), urlDto.getUrl());
        return hash.getHash();
    }
}
