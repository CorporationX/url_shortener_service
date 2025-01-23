package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.url.UrlException;
import faang.school.urlshortenerservice.service.hash.HashCache;
import faang.school.urlshortenerservice.model.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlValidator urlValidator;
    private final HashCache hashCache;

    public String getOriginalUrl(String hash) {
        Optional<String> urlFromCache = urlCacheRepository.get(hash);
        return urlFromCache.orElseGet(() -> {
            String url = urlRepository
                    .findById(hash)
                    .orElseThrow(() -> new EntityNotFoundException("Url by this hash not found not found"))
                    .getUrl();
            urlCacheRepository.save(hash, url);
            return url;
        });
    }

    public String createShort(UrlDto urlDto) {
        String url = urlDto.getUrl();
        if (!urlValidator.isValidUrl(url)) {
            log.error("Url format is incorrect for url: {}", url);
            throw new UrlException("Url " + url + " is not a valid url");
        }
        String hash = hashCache.getFreeHash();
        Url hashedUrl = Url.builder()
                .hash(hash)
                .url(url)
                .build();
        urlRepository.save(hashedUrl);
        urlCacheRepository.save(hash, url);
        return hash;
    }
}
