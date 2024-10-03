package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.Request;
import faang.school.urlshortenerservice.dto.Response;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

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

    @Override
    @Transactional
    public Response createShortUrl(Request dto) {
        String hash = hashCache.getHash();

        if (hash == null || hash.isEmpty()) {
            throw new RuntimeException("Failed to generate a hash");
        }

        urlRepository.save(new Url(hash, dto.getUrl(), LocalDateTime.now()));
        log.info("Saved url: {}", dto.getUrl());

        urlCacheRepository.saveUrlByHash(dto.getUrl(), hash);
        log.info("Saved url: {} by hash: {} in cache", dto.getUrl(), hash);
        return new Response(hash);
    }
}