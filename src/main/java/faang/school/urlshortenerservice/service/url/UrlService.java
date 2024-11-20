package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Transactional(readOnly = true)
    public RedirectView getRedirectView(String hash) {
        return new RedirectView(
                urlCacheRepository.getUrlByHash(hash)
                        .orElseGet(() -> urlRepository.getUrlByHash(hash).map(Url::getUrl)
                                .orElseThrow(() -> new NotFoundException("URL not found for hash: " + hash))));
    }

    @Transactional
    public HashDto generateShortUrl(UrlDto dto) {
        String hash = hashCache.retrieveNextHash();

        if (hash == null || hash.isEmpty()) {
            throw new RuntimeException("Failed to generate a hash");
        }

        urlRepository.save(new Url(hash, dto.getUrl(), LocalDateTime.now()));

        urlCacheRepository.saveUrlByHash(dto.getUrl(), hash);
        return new HashDto(hash);
    }
}