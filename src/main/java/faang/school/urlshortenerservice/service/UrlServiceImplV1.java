package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.enity.FreeHash;
import faang.school.urlshortenerservice.enity.Url;
import faang.school.urlshortenerservice.hash.LocalHash;
import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlServiceImplV1 implements UrlService{
    private final LocalHash localHash;
    private final UrlRepository urlRepository;
    private final HashService hashService;
    private final HashProperties hashProperties;

    @Override
    @Transactional
    public HashDto save(String url) {
        String hash = localHash.getHash();
        Url url1 = Url.builder()
                .url(url)
                .hash(hash)
                .lastGetAt(LocalDateTime.now())
                .build();
        urlRepository.save(url1);
        return new HashDto(hash);
    }

    @Override
    @Transactional
    @Cacheable(value = "hashToUrl", key = "#hash", unless = "#result == null")
    public String get(String hash) {
        Url url = urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("url by hash " + hash + " does not exists"));
        url.setLastGetAt(LocalDateTime.now());
        return url.getUrl();
    }

    @Override
    @Transactional
    public void freeUnusedHash() {
        hashService.saveAll(urlRepository.deleteAndGetUnusedUrl(
                LocalDateTime.now().minusDays(hashProperties.getSaving().getTime().toDays()),
                hashProperties.getGet().getCount()).stream()
                .map(Url::getHash)
                .map(FreeHash::new)
                .toList());
    }
}
