package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCacheService hashCacheService;
    private final UrlRepository urlRepository;

    public String getShortUrl(UrlDto urlDto) {
        String hash = hashCacheService.getHash();

        Url urlEntity = new Url();
        urlEntity.setUrl(urlDto.getUrl());
        urlEntity.setHash(hash);
        urlEntity.setCreatedAt(urlDto.getCreatedAt());

        urlRepository.save(urlEntity);

        return String.format("http://localhost:8080/%s", hash);
    }

    public String getRealUrl(UrlDto urlDto) {
        return urlRepository.findByHash(urlDto.getHash()).getUrl();
    }
}
