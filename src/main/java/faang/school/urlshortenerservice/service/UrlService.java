package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.hashservice.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String createShortLink(UrlDto urlDto) {
        String shortLink = urlRepository.getHash(urlDto.getUrl());
        if (shortLink != null) {
            return shortLink;
        }
        shortLink = hashCache.getHash();
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(shortLink);

        urlCacheRepository.save(url);
        urlRepository.save(url);

        return shortLink;
    }
}
