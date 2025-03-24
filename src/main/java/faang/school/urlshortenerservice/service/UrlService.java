package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.hashes.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${url.protocol}")
    private String protocol;

    @Value("${url.domain}")
    private String host;

    @Value("${server.port}")
    private int port;

    @Transactional
    public String getOriginalUrl(String hash) {
        Optional<String> cacheUrl = urlCacheRepository.get(hash);
        return cacheUrl.orElseGet(() -> {
            String url = urlRepository
                    .findById(hash)
                    .orElseThrow(() -> new UrlNotFoundException("Url by this hash not found not found"))
                    .getUrl();
            urlCacheRepository.save(hash, url);
            return url;
        });
    }

    @Transactional
    public URL getShortUrl(URL url) {
        String hash = hashCache.getHash();
        Url hashedUrl = Url.builder()
                .url(url.toString())
                .hash(hash)
                .build();
        urlRepository.save(hashedUrl);
        urlCacheRepository.save(hashedUrl.getHash(), hashedUrl.getUrl());
        try {
            return new URL(protocol, host, port, "/" + hash);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
