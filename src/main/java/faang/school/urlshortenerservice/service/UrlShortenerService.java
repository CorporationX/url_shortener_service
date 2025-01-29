package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public HashDto shortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .build();
        url = urlRepository.save(url);
        urlCacheRepository.save(url.getHash(), url.getUrl());
        return new HashDto(url.getHash());
    }

    public String getUrlByHash(String hash) {
        Optional<String> cachedUrl = urlCacheRepository.get(hash);
        if (cachedUrl.isPresent()) {
            return cachedUrl.get();
        }

        Optional<Url> savedUrl = urlRepository.findByHash(hash);
        if (savedUrl.isPresent()) {
            return savedUrl.get().getUrl();
        }

        throw new UrlNotFoundException("Url not found by hash", hash);
    }

}
