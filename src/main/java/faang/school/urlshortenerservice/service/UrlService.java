package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    public String createUrl(UrlDto urlDto) {
        log.info("Create url: {}", urlDto.getUrl());
        String hash = hashCache.getHash().getHash();
        Url url = new Url(hash, urlDto.getUrl());
        urlRepository.save(url);
        return hash;
    }

    public String getUrl(String hash) {
        log.info("Get url by hash: {}", hash);
        return urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url by hash '" + hash + "' not found"))
                .getUrl();
    }
}
