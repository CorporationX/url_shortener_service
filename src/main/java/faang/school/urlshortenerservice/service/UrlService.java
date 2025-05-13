package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String getOriginalUrl(String hash) {
        return urlCacheRepository.findUrlByHash(hash)
                .orElseGet(() -> {
                    Url url = urlRepository.findByHash(hash)
                            .orElseThrow(() -> new UrlNotFoundException(hash));
                    urlCacheRepository.save(hash, url.getUrl());
                    return url.getUrl();
                });
    }
}
