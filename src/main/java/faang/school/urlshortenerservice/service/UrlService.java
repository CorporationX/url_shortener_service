package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    @Value("${app.url-config.base-url}")
    private String baseUrl;

    @Transactional
    public ShortUrlDto createShortUrl(Url url) {
        url.setHash("wwwrer");
        log.info("Create new url: {}", url);
        Url savedUrl = urlRepository.save(url);
        log.info("New url: {}", savedUrl);
        urlCacheRepository.saveUrl(savedUrl.getHash(), savedUrl.getUrl());
        return buildShortUrl(savedUrl.getHash());
    }

    private ShortUrlDto buildShortUrl(String hash) {
        String shortUrl = baseUrl.endsWith("/") ? baseUrl + hash : baseUrl + "/" + hash;
        ShortUrlDto shortUrlDto = new ShortUrlDto();
        shortUrlDto.setShortUrl(shortUrl);
        return shortUrlDto;
    }
}
