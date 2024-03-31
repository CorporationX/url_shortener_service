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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    @Transactional
    public UrlDto associateHashWithUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        String url = urlDto.getUrl();

        urlDto.setUrl(formatUrl(url));
        Url entityToSave = urlMapper.toEntity(urlDto);
        entityToSave.setHash(hash);

        Url savedEntity = urlRepository.save(entityToSave);
        log.info("Url redirect have been successfully saved to DB");

        urlCacheRepository.save(savedEntity);
        log.info("Url redirect have been successfully saved to Redis");

        return urlMapper.toDto(savedEntity);
    }

    @Transactional
    public Url getOriginalUrl(String hash) {
        Optional<Url> urlFromCache = urlCacheRepository.get(hash);
        if (urlFromCache.isPresent()) {
            return urlFromCache.get();
        }
        Url urlFromDB = urlRepository.findByHash(hash)
                .orElseThrow(() -> new UrlNotFoundException(String.format("URL not found in DB by hash %s", hash)));
        urlCacheRepository.save(urlFromDB);
        log.info("Successful saved URL from DB in cache");
        return urlFromDB;
    }

    private String formatUrl(String url) {
        String urlSchemaRegex = "^(https?://)";
        String formattedUrl = url.trim()
                .replaceFirst(urlSchemaRegex, "");
        return formattedUrl.endsWith("/") ? formattedUrl.substring(0, formattedUrl.length() - 1) : formattedUrl;
    }
}
