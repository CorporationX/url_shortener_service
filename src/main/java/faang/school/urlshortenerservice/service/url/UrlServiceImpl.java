package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${url.short_prefix}")
    private final String shortUrlPrefix;

    @Transactional
    @Override
    public ShortUrlDto createShortUrl(UrlDto urlDto) {
        log.info("Start create Short Url");
        String hash = hashCache.getHash();
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hash);
        urlRepository.save(url);
        String shortUrl = shortUrlPrefix + hash;
        urlCacheRepository.save(hash, url);
        return (ShortUrlDto.builder()
                .shortUrl(shortUrl).build());
    }


    @Override
    public UrlDto getUrl(UrlDto urlDto) {
        Url urlFromCache = urlCacheRepository.get(urlDto.getHash());
        if (urlFromCache == null) {
            log.debug("Url for hash: {} was not found in cache, start searching in url Repo",
                    urlDto.getHash());
            Url urlFromRepo = urlRepository.findById(urlDto.getHash())
                    .orElseThrow(() -> {
                        log.error("Url for Hash: {} was not found in url repo",
                                urlDto.getHash());

                        return new EntityNotFoundException();

                    });
            return urlMapper.tDto(urlFromRepo);
        }
        return urlMapper.tDto(urlFromCache);
    }
}