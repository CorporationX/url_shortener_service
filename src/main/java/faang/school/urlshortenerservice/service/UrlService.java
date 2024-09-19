package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository cacheRepository;
    @Value("${url-shortener.url}")
    private final String hostName;

    public String add(String url) {
        String hash = hashCache.getHash().getHash();
        Url urlEntity = Url.builder().url(url).hash(hash).build();
        urlRepository.save(urlEntity);
        cacheRepository.put(hash, url);
        return hostName + hash;
    }

    public String get(String hash) {
        String url = cacheRepository.get(hash);
        if (url == null) {
            url = urlRepository.findByHash(hash)
                    .orElseThrow(() -> new EntityNotFoundException("URL on " + hostName +":"+ hash + " not found!"))
                    .getUrl();
        }
        return url;
    }
}
