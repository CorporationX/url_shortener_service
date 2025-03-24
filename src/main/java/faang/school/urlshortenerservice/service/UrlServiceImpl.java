package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${app.older_than_year}")
    @Setter
    private int olderThanYear;

    @Override
    @Transactional
    public void cleanOutdatedUrls() {
        LocalDateTime date = LocalDateTime.now().minusYears(olderThanYear);
        List<String> hash = urlRepository.deleteOutdatedUrls(date);
        log.info("Deleted {} rows from url table.", hash.size());
        hashRepository.saveAll(hash.stream().map(Hash::new).toList());
        log.info("Saved {} rows to hash table.", hash.size());
    }

    @Override
    public String getUrl(String hash) {
        String hashForUrl = urlCacheRepository.getByHash(hash);
        if (!hashForUrl.isEmpty()) {
            log.info("Founded url {} from Redis", hashForUrl);
            return hashForUrl;
        }
        Url url = urlRepository.findByHash(hash).orElseThrow(() -> {
            throw new DataNotFoundException("There is no free hash in base.");
        });
        log.info("Founded url {} from url repository", url.getUrl());
        return url.getUrl();
    }

    @Override
    public UrlDto shortenUrl(UrlDto urlDto) {
        Hash hash = hashCache.getHash();
        if (hash == null) {
            throw new DataNotFoundException("There is no free hash in base.");
        }
        log.info("Received new hash {} for url {}", hash, urlDto.getUrl());
        urlRepository.save(new Url(hash.getHash(), urlDto.getUrl()));
        urlCacheRepository.save(hash.getHash(), urlDto.getUrl());
        return new UrlDto(urlDto.getUrl());
    }
}
