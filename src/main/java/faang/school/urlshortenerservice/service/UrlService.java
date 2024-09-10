package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.сache.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    public String save(UrlDto urlDto) {
        Hash hash = hashCache.getHash();
        Url url = Url.builder()
                .url(urlDto.getUrl())
                .hash(hash.getHash())
                .build();

        urlCacheRepository.saveUrl(urlRepository.save(url));
        return hash.getHash();
    }
}
