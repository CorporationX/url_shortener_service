package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;
    private final HashCache hashCache;

    public UrlDto create(UrlDto urlDto) {
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hashCache.getHash().getHash());
        urlRepository.save(url);
        urlCacheRepository.set(url.getHash(), url.getUrl());
        return urlMapper.toDto(url);
    }
}
