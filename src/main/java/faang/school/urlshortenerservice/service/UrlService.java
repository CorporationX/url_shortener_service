package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String shorten(UrlDto urlDto) {
        String hash = hashCache.getHash().getHash();

        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .build();

        Url save = urlRepository.save(url);
        urlCacheRepository.save(save);
        return hash;
    }

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        String url = urlCacheRepository.getUrl(hash);

        if (url == null) {
            log.info("url not found in cache");

            Url urlRep = urlRepository.findByHash(hash).orElseThrow(() -> new EntityNotFoundException("Url not found"));
            url = urlRep.getUrl();
        }
        return url;
    }
}
