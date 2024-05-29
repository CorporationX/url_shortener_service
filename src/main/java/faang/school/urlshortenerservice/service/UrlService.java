package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Hash;
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
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    @Transactional
    public UrlDto createShortUrl(UrlDto urlDto) {
        String  hash = hashCache.getHash();
        urlCacheRepository.putUrl(hash, urlDto.getUrl());
        Url url = new Url();
        url.setHash(hash);
        url.setUrl(urlDto.getUrl());
        Url saveUrl = urlRepository.save(url);
        return urlMapper.toDto(saveUrl);
    }

    public RedirectView getRedirectView(String hash) {
        String url = urlCacheRepository.getUrl(hash)
                .orElseGet(() -> urlRepository.findById(hash)
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id %s not found", hash)))
                        .getUrl());
        return new RedirectView(url);
    }

}
