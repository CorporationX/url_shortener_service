package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cashe.HashCash;
import faang.school.urlshortenerservice.dto.UrlDtoRequest;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCash hashCash;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String getShortUrl(UrlDtoRequest request) {
        String hash = hashCash.getHash().join();
        saveShortUrl(hash, request);
        return hash;
    }


    private void saveShortUrl(String hash, UrlDtoRequest request) {
        Url url = Url.builder()
                .url(request.getUrl())
                .hash(hash)
                .build();
        urlRepository.save(url);
        urlCacheRepository.saveUrlByHash(hash, request.getUrl());
    }

}
