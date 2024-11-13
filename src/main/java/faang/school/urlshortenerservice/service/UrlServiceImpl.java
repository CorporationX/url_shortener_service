package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.CacheService;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final CacheService cache;

    @Override
    public String getLongUrlByHash(String hash) {
        return cache.getFromCache(hash)
                .or(() -> urlRepository.findUrlByHash(hash))
                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash %s".formatted(hash)));
    }
}