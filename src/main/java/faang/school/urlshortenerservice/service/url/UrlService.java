package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    @Transactional
    public UrlDto getShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        urlCacheRepository.putUrl(hash, urlDto.getUrl());
        urlRepository.save(Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .build());
        return UrlDto.builder().url(hash).build();
    }

    @Transactional(readOnly = true)
    public RedirectView getRedirectUrl(String hash) {
        String url = urlCacheRepository.getUrl(hash)
                .orElseGet(() -> urlRepository.findById(hash)
                        .orElseThrow(() -> new EntityNotFoundException("No url found for hash: " + hash))
                        .getUrl());
        return new RedirectView(url);
    }
}
