package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache cache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository cacheRepository;

    @Transactional
    public String shorten(UrlRequestDto requestDto) {
        Url shortenUrl = new Url();
        String hash = cache.getHash();
        String url = requestDto.getUrl();

        shortenUrl.setUrl(url);
        shortenUrl.setHash(hash);
        shortenUrl.setCreatedAt(LocalDateTime.now());

        urlRepository.save(shortenUrl);
        cacheRepository.save(hash, url);

        return hash;
    }

    public String findUrlByHash(String hash) {
        return cacheRepository.findByHash(hash).orElseGet(() -> findUrlInDB(hash));

    }

    private String findUrlInDB(String hash) {
        return urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFoundException(hash));
    }
}
