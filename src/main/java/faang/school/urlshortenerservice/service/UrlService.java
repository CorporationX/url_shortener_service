package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Cacheable(value = "url", key = "#hash")
    public String getUrl(String hash) {
        Url url = urlRepository.findByHash(hash);
        if (url == null) {
            throw new ResourceNotFoundException("No such url: " + hash);
        }
        return url.getUrl();
    }

    public String createHashCache(UrlDto urlDto) {
        Url url = new Url();
        url.setHash(hashCache.getHash());
        urlRepository.save(url);
        return url.getHash();
    }

}
