package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    public UrlService(UrlRepository urlRepository, HashCache hashCache) {
        this.urlRepository = urlRepository;
        this.hashCache = hashCache;
    }

    public String shortenUrl(String longUrl) {
        String hash = hashCache.getHash(longUrl);
        if (hash != null) {
            return "http://short.url/" + hash;
        }
        hash = generateUniqueHash();
        urlRepository.save(new Url(hash, longUrl));
        hashCache.putHash(longUrl, hash);
        return "http://short.url/" + hash;
    }
}
