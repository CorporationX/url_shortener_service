package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.UrlShortenerBuilder;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.ShortUrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Override
    public List<Url> pollOldUrls(LocalDateTime expired) {
        return urlCacheRepository.pollExpires(expired);
    }

    @Override
    public ShortUrlResponseDto createHashedUrl(String url) {
        String newHash = hashCache.getHash();

        urlCacheRepository.saveUrl(new Url(newHash, url, LocalDateTime.now()));
        String fullHashedUrl = new UrlShortenerBuilder(newHash).fullHashedUrl();
        return new ShortUrlResponseDto(fullHashedUrl);
    }

    @Override
    public String getRealUrlByHash(String hash) {
        return urlCacheRepository.findByHash(hash).getUrl();
    }
}
