package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.UrlAssociation;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validate.UrlValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    private final UrlValidate validator;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String generateShortUrl(UrlDto urlDto) {
        URL url = validator.getValidUrl(urlDto.getOriginalUrl());
        String hash = hashCache.getHash();

        UrlAssociation urlAssociation = new UrlAssociation();
        urlAssociation.setUrl(urlDto.getOriginalUrl());
        urlAssociation.setHash(hash);

        urlRepository.save(urlAssociation);
        urlCacheRepository.save(urlAssociation);

        return url.getProtocol() + "://" + url.getHost() + "/" + hash;
    }

    public String returnFullUrl(String shortUrl) {
        URL url = validator.getValidUrl(shortUrl);
        String hashFromShortUrl = url.getPath().substring(1);

        String originUrl = urlCacheRepository.getOriginUrl(hashFromShortUrl);
        if (originUrl != null) {
            return originUrl;
        } else {
            UrlAssociation urlAssociation = urlRepository.findById(hashFromShortUrl).orElseThrow(
                    () -> new IllegalStateException("For the specified hash the full URL is not in the database"));
            return urlAssociation.getUrl();
        }
    }
}
