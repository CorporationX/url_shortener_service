package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UrlService {

    private final UrlRepository urlRepository;

    @Cacheable(value = "urls", key = "#hash")
    public String shortenUrl(String hash) {
        System.out.println("db hash: " + hash);
        return urlRepository.findUrlByShortUrl(hash).getLongUrl();
    }

}
