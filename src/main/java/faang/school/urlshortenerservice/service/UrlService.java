package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlMapper urlMapper;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String getUrl(String hash) {
        return urlCacheRepository
                .get(hash)
                .orElseGet(() -> {
                    Url url = urlRepository.findById(hash)
                            .orElseThrow(() -> new EntityNotFoundException(String.format("Url %s not found for hash", hash)));
                    urlCacheRepository.save(url);
                    return url;
                })
                .getUrl();
    }

    @Transactional
    public UrlDto generateShortUrl(UrlDto urlDto) {
        urlDto.setHash(hashCache.getHash());
        Url url = urlMapper.toEntity(urlDto);

        Url savedUrl = urlRepository.save(url);
        urlCacheRepository.save(savedUrl);
        return urlMapper.toDto(savedUrl);
    }
}