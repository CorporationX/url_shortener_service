package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.OriginalUrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.UrlAssociation;
import faang.school.urlshortenerservice.localcache.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;


    public Hash saveUrlAssociation(OriginalUrlDto originalUrlDto) {
        Hash hash = hashCache.getHash();
        urlRepository.save(new UrlAssociation(hash.getHash(), originalUrlDto.getUrl()));
        urlCacheRepository.saveUrl(hash.getHash(), originalUrlDto.getUrl());
        return hash;
    }

    @Cacheable(value = "urlCache", key = "#hash")
    public String getUrlByHash(String hash) {
        Optional<UrlAssociation> foundUrlAssociation = urlRepository.findById(hash);
        return foundUrlAssociation.map(UrlAssociation::getUrl).orElse(null);
    }
}
