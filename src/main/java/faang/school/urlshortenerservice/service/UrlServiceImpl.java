package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.OriginalUrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    @Value("${server.base-url}")
    private String baseUrl;

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository cacheRepository;

    @Override
    @Transactional
    public String saveShortUrlAssociation(UrlDto dto) {
        Optional<Url> optionalUrl = urlRepository.findByUrl(dto.getUrl());
        return optionalUrl.map(url -> baseUrl + url.getHash())
                .orElseGet(() -> getHashAndSaveUrl(dto));
    }

    @Override
    public String getOriginalUrl(String hash) {
        Optional<Url> oUrl = cacheRepository.getUrl(hash);
        if (oUrl.isPresent()) {
            return oUrl.get().getUrl();
        } else {
            return urlRepository.findByHash(hash).
                    orElseThrow(() -> new OriginalUrlNotFoundException("not found url with hash " + hash)).getUrl();
        }
    }

    private String getHashAndSaveUrl(UrlDto dto) {
        Url url = new Url();
        url.setUrl(dto.getUrl());
        url.setHash(hashCache.getHash());

        urlRepository.save(url);
        log.info("url {} saved to database", url);
        cacheRepository.saveUrl(url);

        return baseUrl + url.getHash();
    }
}
