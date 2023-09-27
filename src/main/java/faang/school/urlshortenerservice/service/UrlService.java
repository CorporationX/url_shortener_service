package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String shorten(UrlDto urlDto) {
        String hash = hashCache.getHash(urlDto.getUrl());

        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(url);
        return hash;
    }
}
