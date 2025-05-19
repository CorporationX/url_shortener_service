package faang.school.urlshortenerservice.andreev.service.hash;

import faang.school.urlshortenerservice.andreev.cache.HashCache;
import faang.school.urlshortenerservice.andreev.dto.UrlRequestDto;
import faang.school.urlshortenerservice.andreev.dto.UrlResponseDto;
import faang.school.urlshortenerservice.andreev.entity.Url;
import faang.school.urlshortenerservice.andreev.exception.InvalidUrlException;
import faang.school.urlshortenerservice.andreev.exception.UrlNotFound;
import faang.school.urlshortenerservice.andreev.mapper.UrlMapper;
import faang.school.urlshortenerservice.andreev.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static faang.school.urlshortenerservice.andreev.exception.ErrorMessage.INVALID_URL;
import static faang.school.urlshortenerservice.andreev.exception.ErrorMessage.URL_NOT_FOUND_BY_HASH;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;


    @Override
    @Transactional
    @Cacheable(value = "hash", key = "#hash")
    public ResponseEntity<String> redirectToOriginalUrl(String hash) {
        Url url = urlRepository.findByHash(hash).orElseThrow(() ->
                new UrlNotFound(String.format(URL_NOT_FOUND_BY_HASH, hash)));
        log.info("Get url: {} by hash: {}", url, hash);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(url.getUrl()))
                .build();
    }

    @Override
    @Transactional
    @Cacheable(value = "urls", key = "#request.url")
    public UrlResponseDto createShortUrl(UrlRequestDto request) {
        String urlRequest = request.getUrl();
        validUrl(urlRequest);

        Url existingUrl = findByOriginalUrl(urlRequest);
        if (existingUrl != null) {
            log.info("Found url: {}", urlRequest);
            return urlMapper.toUrlResponseDto(existingUrl);
        }

        String hash = hashCache.getHash();

        Url url = Url.builder()
                .hash(hash)
                .url(urlRequest)
                .build();
        urlRepository.save(url);
        log.info("Saved url: {}, hash: {}", url, hash);
        return urlMapper.toUrlResponseDto(url);
    }

    private Url findByOriginalUrl(String originalUrl) {
        return urlRepository.findByUrl(originalUrl).orElse(null);
    }

    private void validUrl(String url) {
        try {
            new URL(url).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            log.error(String.format(INVALID_URL, url), e);
            throw new InvalidUrlException(String.format(INVALID_URL, url));
        }
    }
}
