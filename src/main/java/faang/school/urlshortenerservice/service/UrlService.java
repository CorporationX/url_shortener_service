package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    public String shortenUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .build();

        urlCacheRepository.save(hash, urlDto.getUrl());
        urlRepository.save(url);

        return hash;
    }

    public String getUrl(String hash) {
        String url = urlCacheRepository.getByHash(hash);
        if (url != null && !url.isBlank()) {
            return url;
        }

        Url urlFromRepo = urlRepository.findByHash(hash)
                .orElseThrow(() -> new UrlNotFoundException("URL does not exist for this hash"));

        return urlFromRepo.getUrl();
    }

}