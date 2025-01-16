package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    @CachePut(value = "urls", key = "#url.hash")
    public Url createUrl(Url url) {
        return urlRepository.save(url);
    }

    @Cacheable(value = "urls", key = "#hash")
    public Url getUrl(String hash) {

        return urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url not found for hash: " + hash));
    }

    public Url buildUrl(UrlDto urlDto) {
        Url url = urlMapper.toEntity(urlDto);
        url.setHash("fvfd");
        url.setCreatedAt(LocalDateTime.now());

        return url;
    }
}
