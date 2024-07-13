package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    @Value("${shortener.url}")
    private String shortenerUrl;

    @Transactional
    public UrlDto getShortUrl(UrlDto urlDto){
        String hash = hashCache.getHash();
        Url url = Url.builder().
                url(urlDto.getUrl())
                .hash(shortenerUrl + hash)
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(shortenerUrl + hash, urlDto.getUrl());
        return new UrlDto(shortenerUrl + hash);
    }

}
