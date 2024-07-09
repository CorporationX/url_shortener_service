package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.dto.UrlCreatedRequest;
import faang.school.urlshortenerservice.dto.UrlCreatedResponse;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Override
    public UrlCreatedResponse createUrl(UrlCreatedRequest urlCreatedRequest) {
        String hash = hashCache.getHash();
        urlRepository.save(new Url(hash, urlCreatedRequest.getUrl(), null));
        urlCacheRepository.saveUrlByHash(hash, urlCreatedRequest.getUrl());
        return new UrlCreatedResponse(hash);
    }
}
