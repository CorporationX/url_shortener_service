package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.hashes.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Transactional
    public String getOriginalUrl(String hash) {
        String cacheUrl = urlCacheRepository.get(hash)
                .orElseThrow(() -> new UrlNotFoundException("Url was not found in cache redis"));
        if (cacheUrl == null) {
            String urlDataSource = urlRepository.findUrlByHash(hash)
                    .orElseThrow(() -> new UrlNotFoundException("Url was not found in dataSource"));
            urlCacheRepository.save(hash, urlDataSource);
            return urlDataSource;
        }
        return cacheUrl;
    }

    public URL getShortUrl(URL url) {
        String hash = hashCache.getHash();
        Url hashedUrl = Url.builder()
                .url(url.toString())
                .hash(hash)
                .build();
        urlRepository.save(hashedUrl);
        urlCacheRepository.save(hashedUrl.getHash(), hashedUrl.getUrl());
        try {
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), hash);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
