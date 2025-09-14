package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.config.properties.url.UrlProperties;
import faang.school.urlshortenerservice.dto.CreateUrlRequest;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.cache.UrlCache;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final UrlProperties urlProperties;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlCache urlCache;
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Override
    @Transactional
    public String create(CreateUrlRequest dto) {
        String hash = hashCache.getHash();
        Url url = urlMapper.toEntity(dto, hash);
        urlRepository.save(url);
        urlCache.put(hash, dto.url());
        return buildShortUrl(hash);
    }

    @Override
    @Transactional
    public String getOriginalUrl(String hash) {
        String url = urlCache.get(hash);
        if (url != null && !url.isBlank()) {
            return url;
        }
        Url entity = urlRepository.findById(hash)
                .orElseThrow(() -> new UrlNotFoundException(hash));
        urlCache.put(hash, entity.getUrl());
        return entity.getUrl();
    }

    @Override
    @Transactional
    public List<String> cleanOldUrls(LocalDateTime cutoff) {
        List<String> hashes = urlRepository.deleteOldAndReturnHashes(cutoff);
        if (hashes.isEmpty()) {
            return hashes;
        }
        hashRepository.saveAll(hashes.stream()
                        .map(Hash::new)
                        .toList()
        );
        return hashes;
    }

    private String buildShortUrl(String hash) {
        return String.format("%s/%s", urlProperties.domain(), hash);
    }
}
