package faang.school.urlshortenerservice.service.url.impl;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.impl.HashCache;
import faang.school.urlshortenerservice.service.url.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Override
    public List<Url> getAndDeleteOldUrls(LocalDateTime olderThan) {
        log.info("Deleting and return urls older than {}", olderThan);
        List<Url> urls = urlRepository.deleteAndReturnByCreatedAtBefore(olderThan);
        log.info("Deleted urls {}", urls);
        return urls;
    }

    @Override
    public Url createUrl(String longUrl) {
        log.info("Creating short longUrl {}", longUrl);
        Hash hash = hashCache.getHash();

        Url url = urlRepository.save(new Url(hash.getHash(), longUrl, LocalDateTime.now()));
        log.info("Created short url {}", url);
        return url;
    }
}
