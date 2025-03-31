package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCashRepository urlCashRepository;
    private final LocalCasheService localCashe;

    @Value("${scheduler.url_lifetime_days:30}")
    private int url_lifetime_days;

    @Transactional
    public String createShortUrl(String longUrl) {
        String hash = localCashe.getHash();

        Url url = new Url(hash, longUrl);
        url.setDeletedAt(LocalDateTime.now().plusDays(url_lifetime_days));
        urlRepository.save(url);
        urlCashRepository.save(hash, longUrl);

        return hash;
    }

    @Transactional
    public String getOriginalUrl(String hash) {
        return urlCashRepository.findByHash(hash)
                .or(() -> urlRepository.findByHash(hash)
                        .map(url -> {
                            urlCashRepository.save(url.getHash(), url.getUrl());
                            return url.getUrl();
                        }))
                .orElseThrow(() -> new EntityNotFoundException("URL not found for hash : " + hash));
    }

    @Transactional
    public void removeExpiredUrlsAndResaveHashes() {
        LocalDateTime expirationDate = LocalDateTime.now();

        List<String> hashes = urlRepository.deleteOldUrlsAndReturnHashes(expirationDate);

        List<Hash> hashEntities = hashes
                .stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashEntities);
    }
}
