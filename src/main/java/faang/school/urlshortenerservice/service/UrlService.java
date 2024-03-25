package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    private final String URL_SCHEMA_REGEX = "^(https?://)";

    public UrlDto associateHashWithUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        String url = urlDto.getUrl();

        urlDto.setUrl(formatUrl(url));
        Url entityToSave = urlMapper.toEntity(urlDto);
        entityToSave.setHash(hash);

        Url savedEntity = save(entityToSave);
        log.info("Url redirect have been successfully saved to DB");

        urlCacheRepository.save(savedEntity);
        log.info("Url redirect have been successfully saved to Redis");

        return urlMapper.toDto(savedEntity);
    }

    public Url getOriginalUrl(String hash) {
        Url urlFromCache = urlCacheRepository.get(hash)
                .orElseThrow(() -> new UrlNotFoundException(String.format("Not found URL from cache by hash %s", hash)));
        if (urlFromCache != null) {
            return urlFromCache;
        }
        Url urlFromDB = urlRepository.findByHash(hash)
                .orElseThrow(() -> new UrlNotFoundException(String.format("URL not found in DB by hash %s", hash)));
        urlCacheRepository.save(urlFromDB);
        log.info("Successful saved URL from DB in cache");
        return urlFromDB;
    }

    @Transactional
    public Url save(Url entityToSave) {
        return urlRepository.save(entityToSave);
    }

    private String formatUrl(String url) {
        String formattedUrl = url.trim()
                .replaceFirst(URL_SCHEMA_REGEX, "");
        return formattedUrl.endsWith("/") ? formattedUrl.substring(0, formattedUrl.length() - 1) : formattedUrl;
    }
}
