package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    public UrlDto createShortUrl(UrlDto urlDto){
        Url redirectUrl = urlMapper.toEntity(urlDto);
        String hash = hashCache.getHash();
        redirectUrl.setHash(hash);
        Url savedUrl = urlRepository.save(redirectUrl);
        urlCacheRepository.save(savedUrl.getHash(), savedUrl.getUrl());
        return urlMapper.toDto(savedUrl);
    }
}
