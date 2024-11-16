package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache cache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository cacheRepository;

    @Override
    public void saveAssociation(UrlDto dto) {
        if (!urlRepository.existsByUrl(dto.getUrl())) {
            Url url = new Url();
            url.setUrl(dto.getUrl());
            url.setHash(cache.getHash());

            urlRepository.save(url);
            log.info("url {} saved to database", url);
            cacheRepository.saveUrl(url);
        } else {
            log.info("url {} already exist in database", dto);
        }
    }
}
