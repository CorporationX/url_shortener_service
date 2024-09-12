package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepositoryImpl;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepositoryImpl urlCacheRepositoryImpl;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Transactional
    public String shorten(UrlDto urlDto) {
        String originalUrl = urlDto.getOriginalUrl();
        String hash = hashCache.getCache();
        urlCacheRepositoryImpl.save(hash, originalUrl);
        return hash;
    }

    public String getOriginalUrl(String hash) {
        String originalUrl = urlRepository.getUrlByHash(hash);
        if(originalUrl == null) {
            throw new NotFoundException("Original Url for hash: " + hash + "hot found. " +
                    "Check the correctness of the entered data");
        }
        return originalUrl;
    }
}
