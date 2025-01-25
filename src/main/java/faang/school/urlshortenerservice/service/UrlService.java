package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlReq;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.RedisCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final RedisCashRepository redisCashRepository;

    public String createShortUrl(UrlReq urlReq) {
        String hash = hashCache.getHash();
        String url = urlReq.url();
        checkUrl(url);
        urlRepository.save(Url.builder().hash(hash).url(url).build());
        redisCashRepository.save(hash, url);
        return hash;
    }

    private void checkUrl(String url) {
        if(urlRepository.existsByUrl(url)) {
            log.error("Url {} already exists", url);
            throw new IllegalArgumentException(String.format("Url %s already exists", url));
        }
    }

    public String getOriginalUrl(String hash) {
        String url = redisCashRepository.getUrl(hash);
        if(url != null) {
            return url;
        } else {
            return urlRepository.findByHash(hash).orElseThrow(() ->
                     new EntityNotFoundException("URL with hash %s not found".formatted(hash)))
                    .getUrl();
        }
    }
}
