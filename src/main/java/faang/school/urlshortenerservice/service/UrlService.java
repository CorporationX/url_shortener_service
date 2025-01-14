package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.model.UrlEntity;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashGenerator hashGenerator;

    public String createShortUrl(String longUrl) {
        String hashUrl = hashGenerator.generateHash();

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setHashUrl(hashUrl);
        urlEntity.setLongUrl(longUrl);
        urlRepository.save(urlEntity);

        urlCacheRepository.save(hashUrl, longUrl);

        return "http://short.url/" + hashUrl;
    }
}
