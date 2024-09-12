package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache cache;
    private final UrlJpaRepository urlRepository;
    private  UrlMapper urlMapper;

    @Transactional
    public UrlDto getShortUrl(UrlDto url) {
        String hash = cache.getHash().getHash();
        Url savedUrl = urlRepository.save(urlMapper.toEntity(url));
        return urlMapper.toDto(savedUrl);
    }
}
