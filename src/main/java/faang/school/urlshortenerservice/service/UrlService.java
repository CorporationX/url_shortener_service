package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final UrlValidator validator;

    @Transactional
    public String getUrlHash(UrlDto urlDto) {
        String longUrl = urlDto.getUrl();
        log.debug("LongUrl: {}", longUrl);
        validator.validateUrl(urlDto.getUrl());
        String hash = hashCache.getHash();
        StringBuilder builder = new StringBuilder();
        builder.append(removePathAfterThirdSlash(longUrl));
        builder.append(hash);
        Url url = new Url();
        url.setHash(hash);
        url.setUrl(longUrl);
        urlRepository.save(url);
        log.info("LongUrl {} with hash {} was successful saved in base", longUrl, hash);
        urlCacheRepository.saveUrlInCache(hash, url);
        log.info("LongUrl {} with hash {} was successful saved in cash", longUrl, hash);
        return builder.toString();
    }

    @Transactional
    public String getOriginalUrl(String hash) {
        log.debug("hash: {}", hash);
        Url urlFromCache = urlCacheRepository.getUrlFromCache(hash);
        if (urlFromCache == null) {
            Url urlFromRepository = urlRepository.findByHash(hash)
                    .orElseThrow(() -> new DataValidationException("Cannot find longUrl from hash: " + hash));
            urlCacheRepository.saveUrlInCache(hash, urlFromRepository);
            log.info("Url {} was become from base", urlFromRepository.getUrl());
            return urlFromRepository.getUrl();
        }
        log.info("Url {} was become from cache", urlFromCache.getUrl());
        return urlFromCache.getUrl();
    }

    private String removePathAfterThirdSlash(String urlString) {
        int thirdSlashIndex = getThirdSlashIndex(urlString);
        if (thirdSlashIndex != -1) {
            return urlString.substring(0, thirdSlashIndex + 1);
        }
        return urlString;
    }

    private int getThirdSlashIndex(String urlString) {
        int thirdSlashIndex = -1;
        int slashCount = 0;
        for (int i = 0; i < urlString.length(); i++) {
            if (urlString.charAt(i) == '/') {
                slashCount++;
                if (slashCount == 3) {
                    thirdSlashIndex = i;
                    return thirdSlashIndex;
                }
            }
        }
        return thirdSlashIndex;
    }

}
