package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final HashMapper hashMapper;
    public HashDto create(UrlDto urlDto) {

        Hash hash = hashCache.getHashCache();

        Url url = Url.builder()
                .url(urlDto.getUrl())
                .hash(hash.getHash())
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(url);

        return hashMapper.toDto(hash);
    }
}