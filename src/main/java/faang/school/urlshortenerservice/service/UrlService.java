package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cach.HashCache;
import faang.school.urlshortenerservice.entity.AssociationHashUrl;
import faang.school.urlshortenerservice.entity.UrlCache;
import faang.school.urlshortenerservice.repository.UrlCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCashRepository urlCashRepository;

    @Transactional
    public String getHash(String url) {
        String hash = "https://" + hashCache.getHash();
        AssociationHashUrl associationHashUrl = AssociationHashUrl.builder()
                .hash(hash)
                .url(url)
                .createdAt(LocalDateTime.now())
                .build();
        UrlCache urlCache = new UrlCache(url, hash);
        urlRepository.save(associationHashUrl);
        urlCashRepository.save(urlCache);
        return hash;
    }
}
