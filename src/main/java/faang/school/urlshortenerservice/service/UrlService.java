package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashCash;
import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.repository.UrlCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCashRepository urlCashRepository;
    private final HashCash hashCash;
    private final UrlValidator urlValidator;

    public String createShortUrl(String requestUrl) {
        urlValidator.validateUrl(requestUrl);

        String hash = hashCash.getHash();

        urlRepository.save(new UrlEntity(requestUrl, hash));

        urlCashRepository.save(hash, requestUrl);

        return hash;
    }

    public String getUrlByHash(String hash) {
        String url = urlCashRepository.getUrl(hash);
        if (url != null) {
            return url;
        }
        UrlEntity urlEntity = urlRepository.findByHash(hash);
        urlValidator.validateSearchUrl(urlEntity, hash);
        urlCashRepository.save(hash, urlEntity.getUrl());

        return urlEntity.getUrl();
    }
}
