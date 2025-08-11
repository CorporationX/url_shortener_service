package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ResponseDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlsRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlsRepository urlsRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public ResponseDto createShortUrl(UrlDto request) {
        String hash = hashCache.getHash();
        urlCacheRepository.save(hash, request.getUrl());
        urlsRepository.save(hash, request.getUrl());
        return new ResponseDto(hash, request.getUrl());
    }

    public String getUrl(String hash) {
        String url = urlCacheRepository.findByHash(hash);

        if (url != null) {
            return url;
        }

        try {
            String originalUrl = urlsRepository.getReferenceById(hash).getOriginalUrl();
            urlCacheRepository.save(hash, originalUrl);
            return originalUrl;
        } catch (EntityNotFoundException e) {
            log.info("URL for hash {} not found", hash);
            throw new HashNotFoundException("Url, соответствующий запрошенному хэшу, не найден", hash);
        }
    }
}
