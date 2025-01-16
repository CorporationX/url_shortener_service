package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final UrlValidator validator;

    @Transactional
    public String getUrlHash(UrlDto urlDto) {
        String longUrl = urlDto.getUrl();
        validator.validateUrl(urlDto.getUrl());
        String hash = hashCache.getHash();
        StringBuilder builder = new StringBuilder();
        builder.append(removePathAfterThirdSlash(longUrl));
        builder.append(hash);
        Url url = new Url();
        url.setHash(hash);
        url.setUrl(longUrl);
        urlRepository.save(url);
        urlCacheRepository.save(url);
        return builder.toString();
    }

    @Transactional
    public String getOriginalUrl(String shortUrl) {
        String hash = removePathBeforeThirdSlash(shortUrl);
        Optional<Url> urlFromCache = urlCacheRepository.findByHash(hash);
        StringBuilder builder = new StringBuilder();
        builder.append(removePathAfterThirdSlash(shortUrl));
        if (urlFromCache.isEmpty()) {
            Url urlFromRepository = urlRepository.findByHash(hash)
                    .orElseThrow(() -> new DataValidationException("Cannot find longUrl from hash: " + hash));
            urlCacheRepository.save(urlFromRepository);
            builder.append(urlFromRepository.getUrl());
            return builder.toString();
        }
        return builder.append(urlFromCache.get().getUrl()).toString();
    }

    private String removePathAfterThirdSlash(String urlString) {
        int thirdSlashIndex = getThirdSlashIndex(urlString);
        if (thirdSlashIndex != -1) {
            return urlString.substring(0, thirdSlashIndex + 1);
        }
        return urlString;
    }

    private String removePathBeforeThirdSlash(String urlString) {
        int thirdSlashIndex = getThirdSlashIndex(urlString);
        if (thirdSlashIndex != -1) {
            return urlString.substring(thirdSlashIndex + 1);
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
