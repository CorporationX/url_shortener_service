package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    @Value("${server.url}")
    private String url;

    public UrlResponseDto createShortUrl(String longUrl) {
        String hash = hashCache.getHash();
        Url newUrl = Url.builder()
                .hash(hash)
                .url(longUrl)
                .build();

        urlRepository.save(newUrl);

        urlCacheRepository.save(hash, longUrl);

        return new UrlResponseDto(url + hash);
    }

    public String getLongUrlByHash(String hash) {
        String longUrl = urlCacheRepository.findLongUrlByHash(hash);
        if (longUrl == null) {
            longUrl = urlRepository.findUrlByHash(hash).orElseThrow(() -> new UrlNotFoundException(hash));
            urlCacheRepository.save(hash, longUrl);
        }
        return longUrl;
    }
}
