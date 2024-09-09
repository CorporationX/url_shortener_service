package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.URLDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.URL;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final URLCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final HashMapper hashMapper;
    public HashDto createShortLink(URLDto urlDto){
        Hash hash = hashCache.getCache();

        URL url = URL.builder()
                .url(urlDto.getUrl())
                .hash(hash.getHash())
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(url);

        return hashMapper.toDto(hash);
    }
}
