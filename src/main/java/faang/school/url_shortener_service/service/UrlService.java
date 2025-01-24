package faang.school.url_shortener_service.service;

import faang.school.url_shortener_service.cache.HashCache;
import faang.school.url_shortener_service.dto.UrlRequestDto;
import faang.school.url_shortener_service.dto.UrlResponseDto;
import faang.school.url_shortener_service.entity.Url;
import faang.school.url_shortener_service.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    @Value("${short.url.base}")
    private String baseUrl;


    @Transactional
    @CachePut(key = "#requestDto.originalUrl", value = "urls")
    public UrlResponseDto createShortUrl(UrlRequestDto requestDto) {
        return urlRepository.findByUrl(requestDto.getOriginalUrl())
                .map(existingUrl -> buildResponse(existingUrl.getHash()))
                .orElseGet(() -> {
                    String hash = hashCache.getHash();
                    Url url = new Url(hash, requestDto.getOriginalUrl(), OffsetDateTime.now());
                    urlRepository.save(url);
                    return buildResponse(hash);
                });
    }

    @Cacheable(key = "#hash", value = "urls")
    @Transactional(readOnly = true)
    public String getOriginalURL(String hash) {
        return urlRepository.findById(hash).orElseThrow(() -> new EntityNotFoundException("URL with hash %s not found".formatted(hash)))
                .getUrl();
    }

    private UrlResponseDto buildResponse(String hash) {
        return UrlResponseDto.builder()
                .shortUrl(buildShortUrl(hash))
                .build();

    }

    private String buildShortUrl(String hash) {
        return baseUrl + "/" + hash;
    }
}