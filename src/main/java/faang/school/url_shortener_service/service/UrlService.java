package faang.school.url_shortener_service.service;

import faang.school.url_shortener_service.cache.HashCache;
import faang.school.url_shortener_service.dto.URLRequestDto;
import faang.school.url_shortener_service.entity.Url;
import faang.school.url_shortener_service.repository.url.UrlRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    @Transactional
    @CachePut(key = "#URLRequestDto.hash", value = "urls")
    public String createShortUrl(URLRequestDto requestDto) {
        if (urlRepository.existsByUrl(requestDto.getUrl()))
            throw new EntityExistsException("URL %s already exists".formatted(requestDto.getUrl()));
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .url(requestDto.getUrl())
                .hash(hash)
                .build();
        url.setHash(hash);
        return urlRepository.save(url).getUrl();
    }

    @Cacheable(key = "#hash", value = "urls")
    @Transactional(readOnly = true)
    public String getOriginalURL(String hash) {
        return urlRepository.findById(hash).orElseThrow(() -> new EntityNotFoundException("URL with hash %s not found".formatted(hash)))
                .getUrl();
    }
}