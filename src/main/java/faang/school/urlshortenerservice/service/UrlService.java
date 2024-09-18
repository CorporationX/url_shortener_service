package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlInRedis;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    public final HashCache hashCache;
    public final HashMapper hashMapper;
    public final UrlMapper urlMapper;
    public final UrlRepository urlRepository;
    public final UrlCacheRepository urlCacheRepository;

    @Value("${my_domain_name: https://mydomain.com}")
    private String myDomainName;

    public String createShortUrl(UrlDto longUrl) {
        Optional<Url> urlEntity = urlRepository.findByLongUrl(longUrl.url());
        if (urlEntity.isPresent()) {
            String hashString = urlEntity.get().getHash();
            saveUrlIntoRedis(longUrl, hashString);
            return myDomainName + "/" + hashString;
        }
        String hashString = hashMapper.hashToHashDto(hashCache.getHash()).hash();
        saveUrlIntoRedis(longUrl, hashString);
        saveUrlEntity(longUrl, hashString);
        return myDomainName + "/" + hashString;
    }

    private void saveUrlEntity(UrlDto longUrl, String hash) {
        Url url = new Url();
        url.setLongUrl(longUrl.url());
        url.setHash(hash);
        url.setCreatedAt(LocalDateTime.now());
        urlRepository.save(url);
    }

    private void saveUrlIntoRedis(UrlDto longUrl, String hash) {
        UrlInRedis urlInRedis = new UrlInRedis();
        urlInRedis.setId(hash);
        urlInRedis.setLongUrl(longUrl.url());
        urlInRedis.setCreatedAt(LocalDateTime.now());
        urlCacheRepository.save(urlInRedis);
    }

    public String getOriginUrl(String shortUrl) {
        Optional<UrlInRedis> longUrlFromRedis = urlCacheRepository.findById(shortUrl);
        if (longUrlFromRedis.isPresent()) {
            return longUrlFromRedis.get().getLongUrl();
        }
        Optional<Url> longUrlFromDb = urlRepository.findByHash(shortUrl);
        if (longUrlFromDb.isPresent()) {
            return urlMapper.urlToUrlDto(longUrlFromDb.get()).url();
        }
        throw new IllegalArgumentException("Такого URL не существует. Создайте новый.");
    }
}
