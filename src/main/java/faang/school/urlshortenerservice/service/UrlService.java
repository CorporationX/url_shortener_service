package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String redirectToLongUrl(String shortUrl) {
        Url urlCashRedis = urlCacheRepository.findByHash(shortUrl);
        if (urlCashRedis != null) {
            System.out.println(urlCashRedis.getUrl());
            return urlCashRedis.getUrl();
        } else {
            Url urlBd = urlRepository.findByHash(shortUrl);
            if (urlBd != null) {
                urlCacheRepository.save(urlBd);
                return urlBd.getUrl();
            }
        }
        throw new UrlNotFoundException("Url not found");
    }

}
