package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.LocalCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.Optional.*;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final LocalCache localCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    public String saveAndGetShortUrl(UrlDto urlDto){
        var savedUrlWithHash = saveUrlWithHash(urlDto);

        urlCacheRepository.saveUrl(savedUrlWithHash);

        return savedUrlWithHash.getHash();
    }

    private Url saveUrlWithHash(UrlDto urlDto){
        var hash = localCache.getHash();

        var url = urlMapper.toEntity(urlDto);
        url.setHash(hash);

        return urlRepository.save(url);
    }

    public String getUrl(String hash) {
        return ofNullable(urlCacheRepository.getUrl(hash))
                .orElseGet(() -> urlRepository.findById(hash)
                        .map(Url::getUrl)
                        .orElseThrow(() -> new IllegalArgumentException("No URL find")));
    }
}