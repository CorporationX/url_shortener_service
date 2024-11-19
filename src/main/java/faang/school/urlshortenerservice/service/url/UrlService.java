package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.custom.DataNotFoundException;
import faang.school.urlshortenerservice.model.dto.response.url.ShortenResponse;
import faang.school.urlshortenerservice.repository.RedisCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlValidator urlValidator;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final RedisCacheRepository redisCacheRepository;

    @Value("${url.path}")
    private String urlPath;

    @Transactional
    public ShortenResponse shorten(String url) {
        urlValidator.validateUrl(url);
        String hash = hashCache.getHash();
        urlRepository.save(hash, url);
        redisCacheRepository.savePair(hash, url);

        return ShortenResponse.builder()
                .shortUrl(urlPath.concat(hash))
                .build();
    }

    public String resolve(String hash) {
        String url = redisCacheRepository.getUrl(hash);

        if (url != null) {
            return url;
        }

        try {
            url = urlRepository.findByHash(hash);
        } catch (Exception e) {
            throw new DataNotFoundException(String.format("Cannot resolve url by hash: %s", hash));
        }

        return url;
    }
}
