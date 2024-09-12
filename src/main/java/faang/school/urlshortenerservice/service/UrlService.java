package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlValidator urlValidator;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Transactional
    public UrlDto createShortUrl(UrlDto urlDto) {
        urlValidator.validateUrl(urlDto.getUrl());
        String hash = hashCache.getHash();
        urlCacheRepository.put(hash, urlDto.getUrl());
        Url urlToSave = Url.builder().url(urlDto.getUrl()).hash(hash).build();
        urlRepository.save(urlToSave);

        return UrlDto.builder().url(hash).build();
    }

    @Transactional(readOnly = true)
    public RedirectView getUrl(String hash) {
        String url = urlCacheRepository.get(hash)
                .orElseGet(() -> urlRepository.findById(hash)
                        .orElseThrow(() ->
                                new EntityNotFoundException("Couldn't find url for hash" + hash))
                        .getUrl());
        return new RedirectView(url);
    }
}
