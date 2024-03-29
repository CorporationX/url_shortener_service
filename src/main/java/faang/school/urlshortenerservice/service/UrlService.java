package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String getUrl(String hash) {
        return urlCacheRepository
                .get(hash)
                .orElseGet(() -> {
                    Url url = urlRepository.findById(hash)
                            .orElseThrow(() -> new EntityNotFoundException(String.format("Url %s not found for hash", hash)));
                    urlCacheRepository.save(url);
                    return url;
                })
                .getUrl();
    }
}
