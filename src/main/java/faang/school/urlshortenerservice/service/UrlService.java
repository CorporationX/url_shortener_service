package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.LocalCache;
import faang.school.urlshortenerservice.service.cache.UrlCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final LocalCache localCache;
    private final UrlRepository urlRepository;
    private final UrlCache urlCache;

    @Transactional
    public String createShortUrl(String originalUrl) {
        String hash = localCache.getHash();
        Url newUrl = Url.builder()
                .originalUrl(originalUrl)
                .hash(hash)
                .build();

        return urlRepository.save(newUrl).getHash();
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        return urlCache.getOriginalUrl(hash);
    }

    @Transactional
    public void deleteUrls(List<Url> urls){
        urlRepository.deleteAll(urls);
    }

    @Transactional(readOnly = true)
    public Page<Url> getPageExpiredUrls(LocalDateTime expiredDateTime, PageRequest pageRequest) {
        return urlRepository.findByCreatedAtBefore(expiredDateTime, pageRequest);
    }
}
