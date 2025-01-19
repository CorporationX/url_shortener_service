package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cash.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCasheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCasheRepository urlCasheRepository;
    private final HashCache hashCache;

    @Value("${url.original-path}")
    private String urlPath;

    @Transactional
    public String getOriginalUrl(String hash) {
        String cashedUrl = urlCasheRepository.getUrl(hash);
        if (cashedUrl != null && !cashedUrl.trim().isEmpty()) {
            return cashedUrl;
        }
        try {
            cashedUrl = urlRepository.getUrlByHash(hash);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Url with hash %s was not found in database".formatted(hash));
        }
        return cashedUrl;
    }

    @Transactional
    public String getShotUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        urlRepository.saveUrlWithNewHash(hash, urlDto.getUrl());
        urlCasheRepository.saveUrl(hash, urlDto.getUrl());

        return urlPath.concat(hash);
    }
}
