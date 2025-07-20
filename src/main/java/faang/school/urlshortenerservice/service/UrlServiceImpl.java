package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "urlCache")
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;

    @Override
    @Cacheable(key = "#hash")
    public String findOriginalUrl(String hash) {
        Url foundUrl = urlRepository.findById(hash)
                .orElseThrow(() -> new UrlNotFoundException("Original url for hash %s was not found".formatted(hash)));
        return foundUrl.getUrl();
    }
}