package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;

    public String getUrlByHash(String hash) {
        log.info("getting url by hash {}", hash);
        // TODO сначала идет поиск в redis, через UrlCacheRepository
        Url url = urlRepository.findById(hash).orElseThrow(
                () -> new EntityNotFoundException(String.format("url by hash: %s doesn't exist.", hash)));
        log.info("returning url: {} by hash: {}", url.getUrl(), hash);
        return url.getUrl();
    }
}
