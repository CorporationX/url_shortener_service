package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.DTO.UrlDto;
import faang.school.urlshortenerservice.mappers.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    @CachePut(value = "itemCache", key = "#urlDto.url")
    public UrlDto ShortUrl(UrlDto urlDto){
        Url url = urlMapper.toEntity(urlDto);
        url.setHash("se888s");
        url.setCreatedAt(LocalDateTime.now());
        Url request = urlRepository.save(url);
        return urlMapper.toDto(request) ;
    }

    @Cacheable(value = "itemCache", key = "#hash")
    public String getShortUrl(String hash) {
        return urlRepository.findByHash(hash).getHash();
    }
}
