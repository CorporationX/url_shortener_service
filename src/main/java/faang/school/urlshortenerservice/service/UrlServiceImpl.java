package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlHashCache;
import faang.school.urlshortenerservice.dto.response.FullUrlResponseDto;
import faang.school.urlshortenerservice.entity.UrlHash;
import faang.school.urlshortenerservice.repository.cassandra.UrlHashRepository;
import faang.school.urlshortenerservice.utils.HashCacheFiller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final HashCache hashCache;
    private final HashCacheFiller hashCacheFiller;
    private final UrlHashRepository urlHashRepository;
    private final UrlHashCache urlHashCache;

    @Override
    public String getFullUrl(String hash) {
        return urlHashCache.get(hash);
    }

    @Override
    public FullUrlResponseDto createShortUrl(String fullUrl) {
        String hash = hashCache.get();

        if (hashCache.isNotEnoughHashes()) hashCacheFiller.triggerRefill();

        urlHashRepository.save(new UrlHash(hash, fullUrl));

        urlHashCache.put(hash, fullUrl);

        return new FullUrlResponseDto(hash);
    }
}