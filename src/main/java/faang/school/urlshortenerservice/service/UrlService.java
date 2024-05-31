package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;
    private final HashCache hashCache;
    private final HashRepository hashRepository;

    public UrlDto create(UrlDto urlDto) {
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hashCache.getHash().getHash());
        urlRepository.save(url);
        urlCacheRepository.set(url.getHash(), url.getUrl());
        return UrlDto.builder().url("/" + url.getHash()).build();
    }


    public String get(String hash) {
        Optional<String> optionalUrl = urlCacheRepository.get(hash);
        if (optionalUrl.isPresent()) {
            return optionalUrl.get();
        } else {
            Url url = urlRepository.findFirstByHash(hash).orElseThrow(
                    () -> new EntityNotFoundException("URL not found"));
            urlCacheRepository.set(hash, url.getUrl());
            return url.getUrl();
        }
    }

    @Async
    @Transactional
    public void cleanAsync(){
        log.info("Cleaning Asynchronously");
        clean();
    }

    public void clean(){
        List<Url> urls = urlRepository.cleanOldHashes();
        if (urls != null){
            hashRepository.saveAll(urls.stream().map(url -> new Hash(url.getHash())).toList());
        }
    }
}
