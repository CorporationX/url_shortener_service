package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.model.dto.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.cache.HashCache;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Value("${short_url.domain}")
    private String shortUrlDomain;

    @Override
    @Transactional
    public String createShortUrl(UrlDto urlDto) {
        Url url = Url.builder()
                .url(urlDto.getOriginalUrl())
                .hash(hashCache.getHash()).build();
        urlRepository.save(url);
        return shortUrlDomain + url.getHash();
    }

    @Cacheable(value = "url", key = "#shortUrl")
    @Override
    public String getOriginalUrl(String shortUrl) {
        String[] arr = shortUrl.split(shortUrlDomain);
        Url url = urlRepository.findUrlByHash(arr[1])
                .orElseThrow(() -> new EntityNotFoundException("Can't find url with hash: " + arr[1]));
        return url.getUrl();
    }
}
