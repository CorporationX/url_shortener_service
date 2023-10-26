package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.RedisCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
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
    private final RedisCacheRepository redisCacheRepository;
    private final UrlRepository urlRepository;

    @Transactional
    public String getShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        String shortUrl = buildShortUrl(urlDto, hash).orElseThrow();

        redisCacheRepository.save(hash, urlDto.getUrl());
        urlRepository.save(hash, urlDto.getUrl());

        log.info("The short URL has been successfully created: {}", shortUrl);
        return shortUrl;
    }

    private Optional<String> buildShortUrl(UrlDto urlDto, String hash) {
        URL url;
        try {
            url = new URL(urlDto.getUrl());
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
        String protocol = url.getProtocol();
        String host = url.getHost();
        int port = url.getPort();

        String portString;
        if (port == -1) portString = "";
        else portString = ":" + port;

        return Optional.of(protocol + "://" + host + portString + "/" + hash);
    }
}