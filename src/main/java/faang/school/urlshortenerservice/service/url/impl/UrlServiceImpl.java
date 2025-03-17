package faang.school.urlshortenerservice.service.url.impl;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.service.hash.impl.HashCache;
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
    public Url createUrl(String longUrl) {
        log.info("Creating short longUrl {}", longUrl);
        Hash hash = hashCache.getHash();

        Url url = urlCacheRepository.save(new Url(hash.getHash(), longUrl, LocalDateTime.now()));
        log.info("Created short url {}", url);
        return url;
    }

    @Override
    public String getUrlByHash(String hash) {
        log.info("Getting url by hash {}", hash);
        return urlCacheRepository.findByHash(hash).getUrl();
    }

    @Override
    public String buildUrl(Url url, HttpServletRequest request) {
        String host = request.getServerName();
        int port = request.getServerPort();
        return String.format("%s://%s:%d%s/%s", request.getScheme(), host, port, apiVersion, url.getHash());
    }
}
