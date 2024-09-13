package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UrlService {

    private final UrlRepository urlRepository;

    @Cacheable(value = "urls", key = "#hash")
    public String getUrl(String hash) {
        Url url = urlRepository.findUrlByShortUrl(hash);
        if (url == null) {
            throw new ResourceNotFoundException("No such url: " + hash);
        }
        return url.getShortUrl();
    }

    public void createHashCache(UrlDto urlDto) {
        
    }

}
