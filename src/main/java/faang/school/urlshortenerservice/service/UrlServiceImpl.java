package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exceptions.UrlNotFoundException;
import faang.school.urlshortenerservice.cache.UrlRedisCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final UrlRedisCache urlRedisCache;
    private final HashCache hashCache;

    @Transactional
    @Override
    public UrlDto createShortUrl(UrlDto dto, String domain) {
        String hash = hashCache.getHash();
        urlRepository.save(dto.url(), hash);
        urlRedisCache.save(dto.url(), hash);
        return new UrlDto(domain + hash);
    }

    @Override
    public UrlDto getLongUrl(String hash) {
        Optional<String> longUrlFromCache = urlRedisCache.getUrlByHash(hash);
        if (longUrlFromCache.isPresent()) {
            return new UrlDto(longUrlFromCache.get());
        }

        Optional<String> longUrl = urlRepository.getUrlByHash(hash);
        if (longUrl.isPresent()) {
            urlRedisCache.save(hash, longUrl.get());
            return new UrlDto(longUrl.get());
        }

        throw new UrlNotFoundException("URL not found for hash: " + hash);
    }
}
