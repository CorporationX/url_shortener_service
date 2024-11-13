package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.url.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;
    private final HashCache hashCache;

    @Transactional
    public UrlDto createShortUrl(UrlRequestDto urlRequestDto) {
        UrlDto urlDto = buildUrlDto(generateHash(), urlRequestDto.getUrl());

        saveUrlDto(urlDto);
        cacheUrlDto(urlDto);

        return urlDto;
    }

    private String generateHash() {
        return hashCache.getHash();
    }

    private void saveUrlDto(UrlDto urlDto) {
        Url url = urlMapper.toEntity(urlDto);
        urlRepository.save(url);
    }

    private void cacheUrlDto(UrlDto urlDto) {
        urlCacheRepository.save(urlDto.getHash(), urlDto.getUrl());
    }

    private UrlDto buildUrlDto(String hash, String url) {
        return UrlDto.builder()
                .url(url)
                .hash(hash)
                .build();
    }
}