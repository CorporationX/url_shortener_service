package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repositoy.UrlCacheRepository;
import faang.school.urlshortenerservice.repositoy.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;

    private final UrlCacheRepository urlCacheRepository;

    private final HashCache hashCache;

    private final UrlMapper urlMapper;

    public UrlDto createShortUrl(UrlDto urlDto) {
        Url url = new Url();
        String link = urlDto.getUrl();
        String hash = hashCache.getHash().getHash();

        url.setUrl(link);
        url.setHash(hash);

        urlRepository.save(url);
        urlCacheRepository.putUrl(link, hash);
        return urlMapper.toDto(url);
    }
}
