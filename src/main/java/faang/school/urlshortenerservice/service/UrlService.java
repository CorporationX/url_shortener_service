package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;

    @Transactional
    public String getHash(String url) {
        return urlRepository.findByUrl(url)
                .map(Url::getHash)
                .orElseGet(() -> {
                    String hash = hashCache.getHash();
                    hashRepository.save(Hash.builder().hash(hash).build());
                    Url entity = Url.builder()
                            .hash(hash)
                            .url(url)
                            .build();
                    urlRepository.save(entity);
                    urlCacheRepository.save(hash, url);
                    return hash;
                });
    }

    public String getOriginalUrl(String hash) {
        String url = urlCacheRepository.find(hash);
        if (url != null) {
            return url;
        }
        return urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> {
                    log.error("URL not found for hash: {}", hash);
                    return new UrlNotFoundException(String.format("URL with hash %s not exist!", hash));
                });
    }
}
