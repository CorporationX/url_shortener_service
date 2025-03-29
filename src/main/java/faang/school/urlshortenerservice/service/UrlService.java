package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Value("${server.hash_count}")
    private int hashCount;

    public Url generateShortUrl(Url url) {
        log.info("Generating short URL for: {}", url.getUrl());

        List<Long> randomNumbers = generateUniqueRandomNumbers(hashCount);
        List<String> hashes = hashCache.getHashCache(randomNumbers);

        if (hashes.isEmpty()) {
            throw new RuntimeException("Failed to generate hash for URL");
        }

        String hash = hashes.get(0);
        Url savedUrl = urlRepository.save(url);

        urlCacheRepository.saveUrl(hash, url.getUrl());

        return savedUrl;
    }

    public String getUrl(String hash) {
        String result = urlCacheRepository.getUrl(hash);

        if (!StringUtils.hasText(result)) {
            result = urlRepository.findByHash(hash)
                    .orElseThrow(() ->
                        new RuntimeException("Url record does not exist for the hash supplied!"))
                    .getUrl();

            if (!StringUtils.hasText(result)) {
                throw new UrlNotFoundException(hash);
            }
        }

        return result;
    }

    private List<Long> generateUniqueRandomNumbers(int count) {
        Set<Long> uniqueNumbers = new HashSet<>(count);

        while (uniqueNumbers.size() < count) {
            uniqueNumbers.add(ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE));
        }

        return new ArrayList<>(uniqueNumbers);
    }
}
