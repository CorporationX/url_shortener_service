package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.exeption.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
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
        String hash = hashGenerator.generateHash();

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setHash(hash);
        urlEntity.setLongUrl(longUrl);
        urlRepository.save(urlEntity);

        urlCacheRepository.save(hash, longUrl);

        return "http://short.url/" + hash;
    }

    public String getLongUrl(String hash) {

        String longUrl = urlCacheRepository.find(hash);
        if (longUrl != null) {
            return longUrl;
        }


        return urlRepository.findById(Long.valueOf(hash))
                .map(UrlEntity::getLongUrl)
                .orElseThrow(() -> new UrlNotFoundException("No URL found for hash: " + hash));
    }
}
