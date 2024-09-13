package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.LocalCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final LocalCache localCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    public UrlDto saveAndGetShortUrl(UrlDto urlDto){
        var savedUrlWithHash = saveUrlWithHash(urlDto);

        urlCacheRepository.saveUrl(savedUrlWithHash);

        return urlMapper.toDto(savedUrlWithHash);
    }

    private Url saveUrlWithHash(UrlDto urlDto){
        var hash = localCache.getHash();

        var url = urlMapper.toEntity(urlDto);
        url.setHash(hash);

        return urlRepository.save(url);
    }
}
