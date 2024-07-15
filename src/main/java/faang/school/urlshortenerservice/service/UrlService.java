package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.hashservice.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String createShortLink(UrlDto urlDto) {

        String hash = urlCacheRepository.getHash(urlDto);
        if (hash == null) {
            hash = urlRepository.getHash(urlDto.getUrl());
            if (hash != null) {
                return hash;
            }
            Url url = urlMapper.toEntity(urlDto);
            hash = hashCache.getHash();
            url.setHash(hash);
            urlCacheRepository.saveHash(urlDto, hash);
            urlRepository.save(url);
        }
        return hash;
    }
}
