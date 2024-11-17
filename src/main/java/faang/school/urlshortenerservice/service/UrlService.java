package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
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
    @CachePut(key = "#urlDto.hash", value = "urls")
    public String createShortUrl(UrlDto urlDto) {
        if (urlRepository.existsByUrl(urlDto.getUrl()))
            throw new EntityExistsException("URL %s already exists".formatted(urlDto.getUrl()));

        String hash = hashCache.getHash();
        Url url = Url.builder()
                .url(urlDto.getUrl())
                .hash(hash)
                .build();
        urlDto.setHash(hash);
        return urlRepository.save(url).getUrl();
    }

    @Cacheable(key = "#hash", value = "urls")
    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
            return urlRepository.findById(hash).orElseThrow(() ->
                    new EntityNotFoundException("URL with hash %s not found".formatted(hash)))
                    .getUrl();
    }
}
