package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.GeneralProperties;
import faang.school.urlshortenerservice.config.cache.HashCache;
import faang.school.urlshortenerservice.repository.postgres.url.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import faang.school.urlshortenerservice.validator.AppUrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static faang.school.urlshortenerservice.entity.UrlBuilder.build;

@RequiredArgsConstructor
@Service
public class UrlService {
    private static final String GET_URL_PATH = "/url/";

    private final AppUrlValidator appUrlValidator;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final GeneralProperties generalProperties;

    public String generateShortUrl(String longUrl) {
        appUrlValidator.validate(longUrl);

        String hash = hashCache.getHash(longUrl);

        urlRepository.save(build(hash, longUrl));
        urlCacheRepository.save(hash, longUrl);

        return generalProperties.getAppUrl() + GET_URL_PATH + hash;
    }

    @Transactional
    public List<String> cleanHashes() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        return urlRepository.deleteAndReturnOldUrls(oneYearAgo);
    }
}
