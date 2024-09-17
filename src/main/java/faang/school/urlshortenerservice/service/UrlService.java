package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepositoryImpl;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepositoryImpl urlCacheRepositoryImpl;
    private final HashCache hashCache;

    @Transactional
    public String shorten(UrlDto urlDto) {
        String originalUrl = urlDto.getOriginalUrl();
        Optional<Url> urlOptional = urlRepository.findByOriginalUrl(originalUrl);
        Url url = urlOptional.orElseGet(() -> urlCacheRepositoryImpl.save(hashCache.getHash(), originalUrl));
        String hash = url.hash;
        log.info("Hash for url {} is {}", originalUrl, hash);
        return hash;
    }

    public String getOriginalUrl(String hash) {
        String originalUrl = urlCacheRepositoryImpl.getUrl(hash);
        if (originalUrl == null) {
            throw new NotFoundException("Original Url for hash: " + hash + "hot found. " +
                    "Check the correctness of the entered data");
        }
        return originalUrl;
    }
}
