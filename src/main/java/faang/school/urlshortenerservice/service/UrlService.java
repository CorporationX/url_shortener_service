package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.db.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    @Value("${server.base-url:http://localhost:8080/}")
    private String baseUrl;

    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public UrlDto makeShortLink(UrlDto urlDto) {
        Optional<Url> urlOptional = urlRepository.findByUrl(urlDto.getUrl());
        if (urlOptional.isPresent()) {
            log.info("Found existing hash");
            return buildUrlDto(urlOptional.get().getHash());
        }

        String hash = hashCache.getHash();
        Url url = urlMapper.toEntity(hash, urlDto.getUrl());

        url = urlRepository.save(url);
        log.info("{} saved to DB", url);

        urlCacheRepository.save(hash, url.getUrl());

        return buildUrlDto(hash);
    }

    public String getOriginalUrl(String hash) {
        Optional<String> cachedValue = urlCacheRepository.getUrl(hash);
        if (cachedValue.isPresent()) {
            log.info("Got original url from cache");
            return cachedValue.get();
        } else {
            String url = urlRepository.findById(hash)
                    .orElseThrow(() -> new NotFoundException(String.format("Url by hash %s not found!", hash)))
                    .getUrl();
            log.info("Got original url from DB");
            urlCacheRepository.save(hash, url);
            return url;
        }
    }

    private UrlDto buildUrlDto(String hash) {
        return new UrlDto(baseUrl + hash);
    }
}
