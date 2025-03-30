package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final LocalCache localCache;
    private final UrlCacheRepository cacheRepository;
    private final UrlRepository urlRepository;
    @Value("${shortener.url}")
    private String shortenerUrl;

    @Transactional
    public UrlDto getShortUrl(UrlDto urlDto){

        String hash;
        hash = localCache.getHash();

        Url url = Url.builder()
                .url(urlDto.getUrl())
                .hash(hash)
                .build();

        cacheRepository.save(hash, urlDto.getUrl());
        return new UrlDto(shortenerUrl + hash);
    }

    @Transactional
    public String getLongUrl(String hash) {
        Optional<String> optionalUrl = cacheRepository.findByHash(hash);

        return optionalUrl.orElseGet(() -> urlRepository.getByHash(hash)
                .orElseThrow(() -> new NotFoundException("The url with the " + hash + " hash cannot be found")));
    }
}
