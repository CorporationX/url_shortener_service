package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.UrlAssociation;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validate.UrlValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    private final UrlValidate validator;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    @Value("${spring.url-shortener.service-url}")
    private String serviceUrl;


    public String generateShortUrl(UrlDto urlDto) {
        if (validator.presenceOfUrl(urlDto.getOriginalUrl())) {
            return serviceUrl + urlRepository.findByUrl( urlDto.getOriginalUrl()).getHash();
        }
        String hash = hashCache.getHash();

        UrlAssociation urlAssociation = new UrlAssociation();
        urlAssociation.setUrl(urlDto.getOriginalUrl());
        urlAssociation.setHash(hash);

        urlRepository.save(urlAssociation);
        urlCacheRepository.save(urlAssociation);

        return serviceUrl + hash;
    }

    public String returnFullUrl(String hash) {
        String originUrl = urlCacheRepository.getOriginUrl(hash);
        if (originUrl != null) {
            return originUrl;
        }
        UrlAssociation urlAssociation = urlRepository.findById(hash).orElseThrow(
                () -> new IllegalStateException("For the specified hash the full URL is not in the database"));
        return urlAssociation.getUrl();
    }
}
