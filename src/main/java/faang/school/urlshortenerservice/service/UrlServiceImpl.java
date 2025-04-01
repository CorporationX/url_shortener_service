package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exceptions.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.utils.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService{

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;

    @Value("${spring.url.base-short-url}")
    private final String baseShortUrl;

    @Override
    public String getShortUrl(UrlDto urlDto) {

        String url = urlDto.url();
        String hash = hashCache.getHashFromCache();
        UrlResponseDto newShortUrl = new UrlResponseDto(url, hash, LocalDateTime.now());

        urlCacheRepository.save(hash, url);
        urlRepository.save(urlMapper.toEntity(newShortUrl));
        return baseShortUrl + hash;
    }

    @Override
    public String redirectToRealUrl(String hash) {
        String url = urlCacheRepository.findByHashInRedis(hash);

        if (url != null) {
            log.info("Redirecting from cache: {}", url);
            return url;
        }

        log.info("Not in cache, trying to redirect from DB: {}", hash);
        return urlRepository.findByHash(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFoundException("Url not found for hash " + hash));
    }
}
