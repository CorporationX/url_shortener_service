package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.hashservice.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public String getLongLink(String hash) {
        String longLink = urlCacheRepository.get(hash);
        if (longLink == null) {
            longLink = urlRepository.getUrl(hash);
            if (longLink == null) {
                log.error("Url for hash " + hash + " was not found");
                throw new UrlNotFoundException("Url for hash " + hash + " was not found");
            }
        }
        return longLink;
    }
}
