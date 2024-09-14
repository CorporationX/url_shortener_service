package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import faang.school.urlshortenerservice.repository.URLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final URLRepository urlRepository;
    private final URLCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    @Transactional
    public String create(UrlDto urlDto) {
        String hash = hashCache.getHash();
        urlDto.setHash(hash);

        Url urlToSave = urlMapper.toEntity(urlDto);
        urlRepository.save(urlToSave);

        urlCacheRepository.save(urlDto.getHash(), urlDto.getUrl());

        return hash;
    }

    @Transactional
    public String getByHash(String hash) {
        Optional<String> urlCache = urlCacheRepository.get(hash);

        return urlCache.orElseGet(() -> {
            Optional<Url> urlRepositoryById = urlRepository.findById(hash);

            return urlRepositoryById
                    .map(Url::getUrl)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Hash %s doesn't have url", hash)));
        });
    }

    @Transactional
    public List<String> deleteUrlByDate(LocalDateTime date) {
        return urlRepository.deleteByDate(date).stream()
                .map(Url::getHash)
                .toList();
    }
}
