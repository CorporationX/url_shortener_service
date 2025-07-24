package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.exception.UrlValidateException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private static final String ERROR_DUPLICATE = "duplicate key value violates unique constraint \"url_url_key\"";

    private final UrlMapper urlMapper;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public ResponseUrlDto getShortenedUrl(RequestUrlDto requestUrlDto) {
        if (!isValidURL(requestUrlDto.getUrl())) {
            throw new UrlValidateException("The URL provided is not valid");
        }
        String hash = hashCache.getHash();
        Url url = urlMapper.toEntity(requestUrlDto);
        url.setHash(hash);
        try {
            url = urlRepository.save(url);
        } catch (DataIntegrityViolationException ex) {
            Throwable rootCause = ex.getRootCause();
            if (Objects.requireNonNull(rootCause).getMessage().contains(ERROR_DUPLICATE)) {
                log.info("Duplicate URL detected.");
                url.setHash(getHashByUrl(url.getUrl()));
            }
        }
        urlCacheRepository.cacheHash(url.getHash(), url.getUrl());
        return urlMapper.toDto(url);
    }

    public String getLongUrlByHash(String hash) {
        String originalUrl = urlCacheRepository.getUrl(hash);
        if (originalUrl == null) {
            originalUrl = urlRepository.getLongUrlByHash(hash);
            if (originalUrl == null || originalUrl.isEmpty()) {
                throw new UrlNotFoundException("URL with this hash does not exist");
            }
        }
        return originalUrl;
    }

    private String getHashByUrl(String url) {
        return urlRepository.getHashByUrl(url);
    }

    private boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}
