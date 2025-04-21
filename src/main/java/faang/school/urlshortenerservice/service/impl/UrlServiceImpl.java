package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.ShortUrlResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final HashService hashService;

    @Override
    @Transactional
    public List<Url> pollOldUrls(LocalDateTime expired) {
        List<Url> oldUrls = urlCacheRepository.pollExpires(expired);
        List<Hash> oldHashes = oldUrls.stream()
                .map(url -> {
                    Hash hash = new Hash();
                    hash.setHash(url.getHash());
                    return hash;
                })
                .toList();
        hashService.saveHashBatch(oldHashes);
        return oldUrls;
    }

    @Override
    public ShortUrlResponseDto createShortUrl(String url, HttpServletRequest request) {
        try {
            Url foundHash = urlCacheRepository.findUrl(url);
            if (foundHash != null) {
                String shortUrl = buildShortUrl(foundHash.getHash(), request);
                return new ShortUrlResponseDto(shortUrl);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        String hash = hashCache.getHash();
        Url createdUrl = urlCacheRepository.saveUrl(new Url(hash, url, LocalDateTime.now()));
        String shortUrl = buildShortUrl(createdUrl.getHash(), request);
        return new ShortUrlResponseDto(shortUrl);
    }

    @Override
    public String getRealUrlByHash(String hash) {
        return urlCacheRepository.findUrlByHash(hash).getUrl();
    }

    private String buildShortUrl(String hash, HttpServletRequest request) {
        String scheme = request.getScheme(); // http
        String serverName = request.getServerName(); // localhost
        int serverPort = request.getServerPort();
        String shortUrl = scheme + "://" + serverName + ":" + serverPort + "/" + hash;
        return shortUrl;
    }
}
