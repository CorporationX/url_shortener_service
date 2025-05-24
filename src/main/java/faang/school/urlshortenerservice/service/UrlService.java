package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.RequestDto;
import faang.school.urlshortenerservice.dto.ResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache localHash;
    private final UrlRepository urlRepository;
    private final CacheManager redisCacheManager;
    private final HashRepository hashRepository;


    @Transactional
    public ResponseDto save(RequestDto dto) {
        String hash = localHash.getHash();
        Url url = urlRepository.save(Url.builder().url(dto.url())
                .hash(hash)
                .lastGetAt(LocalDateTime.now()).build());

        redisCacheManager.getCache("hashToUrl").put(hash, dto.url());
        return new ResponseDto(url.getHash());
    }

    @Cacheable(value = "hashToUrl", key = "#hash")
    public String get(String hash) {
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url by hash: " + hash + " not found"))
                .getUrl();
    }


    @Transactional
    public void deleteUnusedHashes(){
         hashRepository.saveAll(urlRepository.deleteUnusedHashes()
                 .stream()
                 .map(Hash::new)
                 .toList());

    }
}