package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception_handler.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${url.base}")
    private String baseUrl;

    public String createShortUrl(String longUrl) {
        String hash = hashCache.getHash();
        urlRepository.save(new Url(hash, longUrl));
        urlCacheRepository.save(hash, longUrl);
        return baseUrl + hash;
    }

    public String findLongUrlByHash(String hash) {
        String url = urlCacheRepository.findByHash(hash);
        if (url != null) {
            return url;
        }

        Optional<Url> urlEntity = urlRepository.findById(hash);
        if (urlEntity.isPresent()) {
            return urlEntity.get().getUrl();
        }

        throw new UrlNotFoundException(hash);
    }
}

