package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    @Value("${server.shorter}")
    private final String shorter;

    @Transactional
    public URL createShortUrl(String urlDto) {
        Hash hash = hashCache.getHash();
        UrlDto longUrlDto = new UrlDto(urlDto);
        urlRepository.save(hash.getHash(), urlDto, LocalDateTime.now());
        urlCacheRepository.save(hash.getHash(), longUrlDto.getUrlDto());

        try {
            return new URL(shorter.concat(hash.getHash()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String getLongUrl(String hash) {
        if (urlCacheRepository.getUrlByHash(hash) != null) {
            return createUrlFromRedis(hash);
        }
        if (urlRepository.getLongUrl(hash) != null) {
            return createUrlFromBd(hash);
        } else {
            throw new UrlNotFoundException("Не найден Url по хешу: " + hash);
        }
    }

    private String createUrlFromRedis(String hash) {
        return urlCacheRepository.getUrlByHash(hash);
    }

    private String createUrlFromBd(String hash) {
        return urlRepository.getLongUrl(hash).getUrl();
    }
}
