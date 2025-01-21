package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;

    public String getShortUrl(UrlDto urlDto) {
        Url url = urlMapper.toEntity(urlDto);
        String hash = hashCache.getHash();
        url.setHash(hash);

        urlCacheRepository.save(hash, urlDto.getUrl());
        urlRepository.save(url);

        return hash;
    }

    public String getOriginalUrl(String hash) {
        String url = urlCacheRepository.get(hash);
        if (url != null && !url.isBlank()) {
            return url;
        }
        Url originalUrl = urlRepository.findByHash(hash)
                .orElseThrow(() -> new UrlNotFoundException("No url found for hash: " + hash));
        return originalUrl.getUrl();
    }

}
