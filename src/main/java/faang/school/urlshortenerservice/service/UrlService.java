package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.utils.HashCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCacheService hashCacheService;
    private final UrlRepository urlRepository;

    public String getShortUrl(UrlDto urlDto) {
        String hash = hashCacheService.getHash();

        Url urlEntity = new Url();
        urlEntity.setUrl(urlDto.getUrl());
        urlEntity.setHash(hash);
        urlEntity.setCreatedAt(LocalDateTime.now());

        urlRepository.save(urlEntity);

        return String.format("https://localhost:8080/url/%s", hash);
    }

    public String getRealUrl(UrlDto urlDto) {
        log.info("Getting real url for hash {}", urlDto.getHash());
        return urlRepository.findByHash(urlDto.getHash()).getUrl();
    }

    public RedirectView redirectToRealUrl(String hash) {
        Url urlEntity = urlRepository.findByHash(hash);

        if (urlEntity != null) {
            log.info("Redirecting to real url");
            return new RedirectView(urlEntity.getUrl());
        } else {
            return new RedirectView("/error");
        }
    }
}
