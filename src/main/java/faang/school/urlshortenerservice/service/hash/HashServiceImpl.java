package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlAlreadyExistsException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.time.LocalDateTime;

import static faang.school.urlshortenerservice.exception.ErrorMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;


    @Cacheable(value = "hash", key = "#hash")
    @Transactional(readOnly = true)
    public String getOriginalUrlByHash(String hash) {
        Url url = urlRepository.findByHash(hash)
                .orElseThrow(() -> new UrlNotFoundException(String.format(URL_NOT_FOUND_BY_HASH, hash)));
        return url.getUrl();
    }

    @Override
    @Transactional
    public ResponseEntity<String> redirectToOriginalUrl(String hash) {
        String originalUrl = getOriginalUrlByHash(hash);
        log.info("Get url: {} by hash: {}", originalUrl, hash);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @Override
    @Transactional
    @Cacheable(value = "urls", key = "#request.url")
    public UrlResponseDto createShortUrl(UrlRequestDto request) {
        String urlRequest = request.getUrl();

        Url existingUrl = findByOriginalUrl(urlRequest);
        if (existingUrl != null) {
            log.info("Found url: {}", urlRequest);
            return urlMapper.toUrlResponseDto(existingUrl);

        }

        try {
            String hash = hashCache.getHash();

            Url url = Url.builder()
                    .hash(hash)
                    .url(urlRequest)
                    .createdAt(LocalDateTime.now())
                    .build();
            urlRepository.save(url);
            log.info("Saved url: {}, hash: {}", url, hash);
            return urlMapper.toUrlResponseDto(url);
        } catch (DataIntegrityViolationException e) {
            log.warn("URL already exists, retrying find: {}", urlRequest);
            Url existing = findByOriginalUrl(urlRequest);
            if (existing != null) {
                return urlMapper.toUrlResponseDto(existing);
            }
            throw new UrlAlreadyExistsException(String.format(URL_ALREADY_EXISTS, urlRequest));
        }
    }

    private Url findByOriginalUrl(String originalUrl) {
        return urlRepository.findByUrl(originalUrl).orElse(null);
    }
}
