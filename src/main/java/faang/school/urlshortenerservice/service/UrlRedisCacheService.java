package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.UrlRedisCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UrlRedisCacheService {
    private final UrlRedisCacheRepository urlRedisCacheRepository;

    public void saveUrl(String hash, String longUrl) {
        urlRedisCacheRepository.saveUrl(hash, longUrl);
    }

    public Optional<String> findByHash(String hash) {
        return urlRedisCacheRepository.findByHash(hash);
    }
}
