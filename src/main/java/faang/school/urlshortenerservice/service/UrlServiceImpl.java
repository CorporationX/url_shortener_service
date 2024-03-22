package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    @Transactional
    public String shortenUrl(UrlDto url) {
        Hash hash = hashCache.getHash();
        urlRepository.save(new Url(hash.getHash(), url.getUrl(), LocalDateTime.now()));
        urlCacheRepository.save(hash.getHash(), url.getUrl());
        return hash.getHash();
    }


    public Url getOriginalUrl(String hash) {
        Url urlFromCache = urlCacheRepository.get(hash)
                .orElseThrow(() -> new UrlNotFoundException(String.format("Url в кэше не найден по хэшу: %s", hash)));

        if (urlFromCache != null) {
            return urlFromCache;
        }
        Url urlFromDb = urlRepository.findByHash(hash)
                .orElseThrow(() -> new UrlNotFoundException(String.format("URL по хешу нигде не найден %s", hash)));
        urlCacheRepository.save(urlFromDb.getHash(), urlFromDb.getUrl());
        return urlFromDb;
    }

}