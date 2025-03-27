package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final RedisService redisService;
    private final HashCache hashCache;

    @Transactional
    public String shortenUrl(String originalUrl) {
        String hash = hashCache.getHash();

        Url url = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .build();

        Url saved = urlRepository.save(url);
        redisService.setValue(hash, originalUrl);
        log.info("Saved new url object: {}", saved);
        return hash;
    }

    @Transactional(readOnly = true)
    public String getUrlByHash(String hash) {
        Optional<String> value = redisService.getValue(hash);
        if (value.isEmpty()) {
            Url found = urlRepository.findByHash(hash)
                    .orElseThrow(() -> new HashNotFoundException("Hash: " + hash + " is not exists in database"));
            redisService.setValue(hash, found.getUrl());
            return found.getUrl();
        }
        return value.get();
    }

    @Transactional
    public void cleanExpiredUrls() {
        List<String> freeHashes = urlRepository.deleteExpiredUrls();
        List<Hash> mapped = freeHashes.stream().map(Hash::new).toList();
        hashRepository.saveAll(mapped);
        log.info("Cleaned expired urls, and added {} free hashes", freeHashes.size());
    }

}
