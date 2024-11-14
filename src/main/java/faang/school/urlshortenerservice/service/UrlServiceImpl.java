package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exceptions.RedisOperationException;
import faang.school.urlshortenerservice.generator.LocalCache;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final LocalCache localCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    @Value("${app.host}")
    private String host;

    @Value("${server.port}")
    private String port;

    @Override
    @Transactional
    public ShortUrlDto shortenUrl(UrlDto urlDto) {
        String hash = localCache.getHash();
        Url url = new Url(hash, urlDto.getUrl());
        url = urlRepository.save(url);
        cacheUrl(url);

        return new ShortUrlDto(buildShortUrl(hash));
    }

    private void cacheUrl(Url url) {
        try {
            urlCacheRepository.save(url);
        } catch (Exception e) {
            throw new RedisOperationException(
                    String.format("Unable to cache url %s in cache", url.getUrl()), e);
        }
    }

    private String buildShortUrl(String hash) {
        return host + ":" + port + "/" + hash;
    }
}
