package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.repository.UrlRedisRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlRedisRepository urlRedisRepository;

    @Transactional
    @Override
    public UrlDto shortenUrl(UrlDto urlDto) {
        String normalizedUrl = urlDto.getUrl();
        String hash = hashCache.getHash();
        Url url = new Url(hash, normalizedUrl, LocalDateTime.now());
        urlRepository.save(url);
        urlRedisRepository.save(hash, url);
        UrlDto urlDto1 = new UrlDto();
        urlDto1.setUrl(String.format("https://faang.school/%s", hash));
        return urlDto1;
    }

    @Transactional(readOnly = true)
    @Override
    public UrlDto getNormalUrl(String hash) {
        Optional<Url> urlFromRedis = urlRedisRepository.find(hash);
        if (urlFromRedis.isPresent()) {
            return new UrlDto(urlFromRedis.get().getUrl());
        } else {
            Url urlFromDB = urlRepository.findByHash(hash).orElseThrow(() -> new EntityNotFoundException("Hash not found"));
            return new UrlDto(urlFromDB.getUrl());
        }
    }
}
