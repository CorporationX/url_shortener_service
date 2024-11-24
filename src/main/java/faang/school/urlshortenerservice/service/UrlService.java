package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public Url createShortUrl(Url url) {
        url.setHash("wwwrer");
        log.info("Create new url: {}", url);
        Url savedUrl = urlRepository.save(url);
        log.info("New url: {}", savedUrl);
        urlCacheRepository.saveUrl(savedUrl.getHash(), savedUrl.getUrl());

        return savedUrl;
    }
}
