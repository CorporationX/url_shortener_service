package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.CreateUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import faang.school.urlshortenerservice.service.cache.UrlCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UrlServiceImpl implements UrlService {
    private final HashCache cache;
    private final UrlRepository urlRepository;
    private final UrlCache urlCache;
    @Value("${url.domain}")
    private String domain;

    @Override
    public String createShortUrl(CreateUrlDto dto) {
        String hash = cache.getHash();
        Url url = Url.builder()
                .url(dto.originalUrl())
                .hash(hash)
                .build();
        urlRepository.save(url);
        urlCache.set(hash, dto.originalUrl());
        return buildShortUrl(hash);
    }

    @Override
    public String getOriginalUrl(String hash) {
        return urlCache.get(hash);
    }

    private String buildShortUrl(String hash) {
        return String.format("%s/%s", domain, hash);
    }
}