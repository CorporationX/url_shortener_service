package faang.school.urlshortenerservice.service.url.impl;

import faang.school.urlshortenerservice.dto.ResponseDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlServiceImpl implements UrlService {
    private static final String URL_FORMAT = "%s://%s:%d%s/%s";
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    @Value("${api-version}")
    private String apiVersion;

    @Override
    public List<Url> getAndDeleteOldUrls(LocalDateTime olderThan) {
        log.info("Deleting and return urls older than {}", olderThan);
        List<Url> urls = urlCacheRepository.deleteAndReturnByCreatedAtBefore(olderThan);
        log.info("Deleted urls {}", urls);
        return urls;
    }

    @Override
    public ResponseDto createShortUrl(String originalUrl, HttpServletRequest request) {
        log.info("Start creating short for url: {}", originalUrl);

        Url url = urlCacheRepository.save(new Url(hashCache.getHash(), originalUrl, LocalDateTime.now()));
        String shortUrl = buildUrl(url, request);

        log.info("Created short url: {}", shortUrl);
        return new ResponseDto(shortUrl);
    }

    @Override
    public String getUrlByHash(String hash) {
        log.info("Getting url by hash {}", hash);
        return urlCacheRepository.findByHash(hash).getUrl();
    }

    private String buildUrl(Url url, HttpServletRequest request) {
        return String.format(URL_FORMAT,
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                apiVersion,
                url.getHash());
    }
}
