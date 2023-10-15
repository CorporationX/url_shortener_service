package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.cache.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    @Transactional
    public String getShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        String shortUrl = buildShortUrl(urlDto, hash).orElseThrow();
        urlCacheRepository.save(hash, urlDto.getUrl());
        urlRepository.save(hash, urlDto.getUrl());
        log.info("Short url was successfully created: {}", shortUrl);
        return shortUrl;
    }

    public String getOriginalUrl(String hash) {
        String cacheUrl = urlCacheRepository.getUrl(hash);
        if (!cacheUrl.isEmpty()) {
            return cacheUrl;
        }
        Optional<String> urlByHash = urlRepository.findUrlByHash(hash);
        if (urlByHash.isPresent()) {
            return urlByHash.get();
        } else {
            log.error("Hash doesn't exist");
            throw new NotFoundException("Hash doesn't exist");
        }
    }

    private Optional<String> buildShortUrl(UrlDto urlDto, String hash) {
        URL url = null;
        try {
            url = new URL(urlDto.getUrl());
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
        String protocol = url.getProtocol();
        String host = url.getHost();
        int port = url.getPort();
        String portString = (port == -1) ? "" : ":" + port;
        return Optional.of(protocol + "://" + host + portString + "/" + hash);
    }
}
