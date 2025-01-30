package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRedisRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlMapper urlMapper;
    private final UrlRedisRepository urlRedisRepository;

    @Value("${url}")
    private String baseUrl;

    public ShortUrlDto generateShortUrl(UrlDto urlDto) {
        log.info("Trying to generate short url for url {}", urlDto);
        String hash = hashCache.getHash();
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hash);
        urlRepository.save(url);
        urlRedisRepository.save(url);
        return urlMapper.toShortUrlDto(url, baseUrl);
    }

    public UrlDto getUrlByShortUrl(ShortUrlDto shortUrlDto) {
        log.info("Trying to get full url for short url {}", shortUrlDto);
        String hash = extractHashFromShortUrl(shortUrlDto.shortUrl());
        Url url = urlRedisRepository.getByHash(hash);
        if (url == null) {
            log.info("Could not find url in redis cache. Trying to get url {} from database", shortUrlDto);
            url = getUrl(shortUrlDto.shortUrl());
        }
        return urlMapper.toUrlDto(url);
    }

    @Transactional
    public void deleteOldShortUrls() {
        List<Url> deletedShortUrls = urlRepository.deleteOldShortUrls();
        List<String> hashes = mapUrlsToHashes(deletedShortUrls);
        hashRepository.save(hashes);
    }

    private String extractHashFromShortUrl(String url) {
        int lastSlashIndex = url.lastIndexOf("/");
        return url.substring(lastSlashIndex + 1);
    }

    private Url getUrl(String id) {
        return urlRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Url under id %s does not exist", id)
                ));
    }

    private List<String> mapUrlsToHashes(List<Url> urls) {
        return urls.stream()
                .map(Url::getHash)
                .toList();
    }
}
