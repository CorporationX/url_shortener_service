package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String shortenUrl(String originalUrl) {
        String hash = hashCache.getHash();
        if (hash == null) {
            throw new RuntimeException("Failed to generate hash: HashCache is empty");
        }
        if (urlRepository.findByHash(hash) != null) {
            throw new RuntimeException("Hash already exists");
        }

        Url url = new Url();
        url.setHash(hash);
        url.setUrl(originalUrl);
        urlRepository.save(url);

        urlCacheRepository.save(hash, originalUrl);
        urlCacheRepository.printValue(hash);

        return "http://short.url/" + hash;
    }
}
