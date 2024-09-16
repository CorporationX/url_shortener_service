package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    public String createUrl(String url) {
        String hash = hashCache.getHash().getHash();
        Url newUrl = new Url(hash, url);
        urlRepository.save(newUrl);
        return hash;
    }
}
