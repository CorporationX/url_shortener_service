package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


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

    public String redirectLongUrl(String hash) {
        return Optional.ofNullable(urlCacheRepository.findByHash(hash))
                .map(result -> result.isBlank() ? findUrlByHash(hash) : result)
                .orElseThrow(() -> new HashNotFoundException("Hash not found"));
    }

    protected String findUrlByHash(String hash) {
        Url url = urlRepository.findByHash(hash);
        if (url == null) {
            throw new HashNotFoundException("Hash not found");
        }
        return url.getUrl();
    }
}
