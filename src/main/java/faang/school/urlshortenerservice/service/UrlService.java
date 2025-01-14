package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.UrlEntity;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String createShortUrl(String longUrl) {
        String hashUrl = UUID.randomUUID().toString().substring(0, 8);

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setHashUrl(hashUrl);
        urlEntity.setLongUrl(longUrl);
        urlRepository.save(urlEntity);

        urlCacheRepository.save(hashUrl, longUrl);

        return "http://short.url/" + hashUrl;
    }
}
