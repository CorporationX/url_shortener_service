package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cach.HashCache;
import faang.school.urlshortenerservice.entity.AssociationHashUrl;
import faang.school.urlshortenerservice.entity.UrlCache;
import faang.school.urlshortenerservice.repository.UrlCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
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
        UrlCache urlCache = new UrlCache(hash, url);
        urlRepository.save(associationHashUrl);
        urlCashRepository.save(urlCache);
        return hash;
    }


    public String getLongUrl(String hash) {
        UrlCache urlCache = urlCashRepository.findByHash(hash);
        if(urlCache == null) {
            try {
                AssociationHashUrl associationHashUrl = urlRepository.findByHash(hash);
                return associationHashUrl.getUrl();
            } catch (EntityNotFoundException e) {
                log.error("Url not found:", e);
                throw new EntityNotFoundException("Url not found");
            }
        }else {
            return urlCache.getUrl();
        }
    }
}
