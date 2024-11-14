package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.RedisCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final RedisCacheRepository redisCacheRepository;

    public String getShortUrl(UrlDto urlDto) {
        if (urlRepository.existsByUrl(urlDto.url()))
            throw new EntityExistsException("URL %s already exists".formatted(urlDto.url()));

        String hash = hashCache.getHash();
        Url url = Url.builder()
                .url(urlDto.url())
                .hash(hash)
                .build();
        redisCacheRepository.save(hash, url.getUrl());
        return urlRepository.save(url).getUrl();
    }
}
