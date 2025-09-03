package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCache;
import faang.school.urlshortenerservice.dto.short_url.CreateShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UrlServiceImpl implements UrlService {
    @Value("${shortener.url.domain}")
    private String domain;

    private final HashCache cache;
    private final UrlRepository urlRepository;
    private final UrlCache urlCache;
    private final UrlMapper urlMapper;

    @Override
    public String createShortUrl(CreateShortUrlDto dto) {
        String hash = cache.getHash();
        Url url = urlMapper.toUrl(dto, hash);

        urlRepository.save(url);
        urlCache.set(hash, dto.url());

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
