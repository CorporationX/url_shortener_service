package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.impl.UrlCacheRepositoryImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepositoryImpl urlCacheRepositoryImpl;

    @Transactional
    public String getHash(UrlDto dto) {
        if (getExistingHash(dto.getUrl()) != null) {
            log.info("Hash for this link already exists: {}", dto.getUrl());
            return getExistingHash(dto.getUrl());
        }
        String hash = hashCache.getHash();
        Url url = new Url();
        url.setUrl(dto.getUrl());
        url.setHash(hash);
        urlRepository.save(url);
        urlCacheRepositoryImpl.add(dto.getUrl(), hash);
        return hash;
    }

    public String getExistingHash(String url) {
        return urlCacheRepositoryImpl.getHash(url);
    }

}
