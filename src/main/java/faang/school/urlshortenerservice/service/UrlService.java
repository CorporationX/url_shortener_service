package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${app.base_url}")
    private String baseHostUrl;

    public String getShortUrl(UrlDto urlDto) {
        String existingHash = urlCacheRepository.getHashByUrl(urlDto.getUrl());
        if (existingHash != null) {
            return String.format("%s/%s", baseHostUrl, existingHash);
        }

        existingHash = urlRepository.findHashByUrl(urlDto.getUrl());
        if (existingHash != null) {
            urlDto.setHash(existingHash);
            urlCacheRepository.save(urlDto);
            return String.format("%s/%s", baseHostUrl, existingHash);
        }

        String hash = hashCache.getHash();
        urlDto.setHash(hash);
        urlRepository.save(urlMapper.toEntity(urlDto));
        urlCacheRepository.save(urlDto);
        return String.format("%s/%s", baseHostUrl, hash);
    }
}
