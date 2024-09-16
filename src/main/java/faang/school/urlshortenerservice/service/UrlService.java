package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.generator.HashCache;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCash;
    private final UrlRepository urlRepository;
    private final UrlCacheService urlCacheService;
    private final MessageSource messageSource;

    public String getLongUrl(String shortUrl) {
        String longUrl = urlCacheService.getCachedLongUrl(shortUrl);

        if (longUrl == null) {
            Url urlEntity = urlRepository.findByHash(shortUrl);

            if (urlEntity != null) {
                longUrl = urlEntity.getUrl();
                urlCacheService.cacheLongUrl(shortUrl, longUrl);
            } else {
                throw new EntityNotFoundException(messageSource.getMessage("exception.entity_not_found", null,
                        Locale.getDefault()));
            }

        }

        return longUrl;
    }

    public String getShortUrl(UrlDto urlDto) {
        String hash = hashCash.getHash();
        Url urlEntity = new Url(hash, urlDto.getUrl());
        urlRepository.save(urlEntity);
        return hash;
    }

}
