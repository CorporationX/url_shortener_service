package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;
    private final HashCache hashCache;

    public UrlDto create(UrlDto urlDto) {
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hashCache.getHash().getHash());
        urlRepository.save(url);
        urlCacheRepository.set(url.getHash(), url.getUrl());
        return urlMapper.toDto(url);
    }


    public UrlDto get(String hash) {
        UrlDto urlDto;
        Optional<String> optionalUrlDto = urlCacheRepository.get(hash);
        if (optionalUrlDto.isPresent()) {
            urlDto = new UrlDto(hash, optionalUrlDto.get());
        } else {
            Url url = urlRepository.findFirstByHash(hash).orElseThrow(
                    () -> new EntityNotFoundException("URL not found"));
            urlDto = urlMapper.toDto(url);
            urlCacheRepository.set(hash, url.getUrl());
        }
        return urlDto;
    }
}
