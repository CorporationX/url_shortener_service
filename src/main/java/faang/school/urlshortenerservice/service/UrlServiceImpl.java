package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlShortDto;
import faang.school.urlshortenerservice.exceptions.UrlNotFoundException;
import faang.school.urlshortenerservice.cache.UrlCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final UrlCache urlCache;
    private final HashCache hashCache;

    @Transactional
    @Override
    public UrlShortDto createShortUrl(UrlDto dto) {
        String hash = hashCache.getHash();
        urlRepository.save(dto.url(), hash);
        urlCache.save(dto.url(), hash);
        return new UrlShortDto("https://short.url/" + hash);
    }

    @Override
    public UrlDto getLongUrl(String hash) {
        Optional<String> longUrlFromCache = urlCache.getUrlByHash(hash);
        if (longUrlFromCache.isPresent()) {
            return new UrlDto(longUrlFromCache.get());
        }

        Optional<String> longUrl = urlRepository.getUrlByHash(hash);
        if (longUrl.isPresent()) {
            urlCache.save(hash, longUrl.get());
            return new UrlDto(longUrl.get());
        }

        throw new UrlNotFoundException("URL not found for hash: " + hash);
    }
}
