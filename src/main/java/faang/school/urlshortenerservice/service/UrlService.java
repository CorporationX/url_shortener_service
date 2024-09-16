package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final URLCacheRepository urlCacheRepository;

    @Transactional
    public String getShortUrl(UrlDto urlDto) {

        String hash = hashCache.getHash();

        Url url = Url.builder()
                .url(urlDto.getUrl())
                .hash(hash)
                .build();

        Url savedUrl = urlRepository.save(url);
        urlCacheRepository.saveUrl(savedUrl);

        return hash;
    }
}
