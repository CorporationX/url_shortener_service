package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${spring.url.ttl-in-days}")
    private int ttlInDays;
    private final UrlRepository urlRepository;
    private final HashService hashService;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String getShortUrl(UrlDto urlDto) {
        Url url = prepareUrl(urlDto);
        urlCacheRepository.save(url.getHash(), url.getUrl());
        return urlRepository.save(url).getHash();
    }

    @Transactional(readOnly = true)
    public RedirectView getOriginalUrlAndRedirect(String hash) {
        return new RedirectView(getOriginalUrl(hash));
    }

    @Transactional
    public void deleteOldEntriesAndRetrieveUnusedHashes() {
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(ttlInDays);
        List<Url> oldUrls = urlRepository.findAllByCreatedAtBefore(expirationDate);
        List<String> hashes = oldUrls.stream()
                .map(Url::getHash)
                .toList();
        hashService.batchSave(hashes);
        urlRepository.deleteAllByCreatedAtBefore(expirationDate);
    }

    private Url prepareUrl(UrlDto urlDto) {
        return Url.builder()
                .hash(hashService.getHash())
                .url(urlDto.getUrl())
                .build();
    }

    @Transactional(readOnly = true)
    protected String getOriginalUrl(String hash) {
        return urlCacheRepository.get(hash).orElseGet(() -> urlRepository.findByHash(hash).orElseThrow(() -> {
            log.error("Unable to find original url for {}", hash);
            return new RuntimeException("Unable to find original url for " + hash);
        }).getUrl());
    }
}