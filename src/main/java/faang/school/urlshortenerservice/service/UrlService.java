package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exeption.url.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashRepository hashRepository;

    @Cacheable(value = "urlCache", key = "#hash.hash")
    public Url getOriginalUrl(Hash hash) {
        try {
            return hashRepository.getUrlByHash(hash.getHash());
        } catch (UrlNotFoundException e) {
            log.error("Exception occurred: {}", e.getMessage());
            throw new UrlNotFoundException("URL not found for hash: " + hash.getHash());
        }
    }
}
