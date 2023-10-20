package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    private final String URL_SCHEMA_REGEX = "^(https?://)";

    public UrlDto associateHashWithURL(UrlDto urlDto) {
        String hash = hashCache.getHash();
        String url = urlDto.getUrl();

        urlDto.setUrl(formatUrl(url));
        Url entityToSave = urlMapper.toEntity(urlDto);
        entityToSave.setHash(hash);

        Url entity = save(entityToSave);
        log.info("Link and it's corresponding redirect have been successfully saved to Postgres");

        urlCacheRepository.save(entity);
        log.info("Link and it's corresponding redirect have been successfully saved to Redis");

        return urlMapper.toDto(entity);
    }

    @Transactional
    public Url save(Url url) {
        return urlRepository.save(url);
    }

    public Url getOriginalUrl(String hash) {
        Url urlFromCache = urlCacheRepository.get(hash)
                .orElseThrow(() -> new UrlNotFoundException(String.format("Not found URL from cache by hash %s", hash)));
        if (urlFromCache != null) {
            return urlFromCache;
        }
        Url urlFromDb = urlRepository.findByHash(hash)
                .orElseThrow(() -> new UrlNotFoundException(String.format("Not found URL from DB by hash %s", hash)));
            urlCacheRepository.save(urlFromDb);
        throw new UrlNotFoundException("Url wasn't found anywhere!");
    }

    private String formatUrl(String url) {
        String formattedUrl = url.trim()
                .replaceFirst(URL_SCHEMA_REGEX, "");
        return formattedUrl.endsWith("/") ? formattedUrl.substring(0, formattedUrl.length() - 1) : formattedUrl;
    }
}