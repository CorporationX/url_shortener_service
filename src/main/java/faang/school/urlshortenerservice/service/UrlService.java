package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Data
@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    @Transactional
    public void saveUrl(UrlDto urlDto) {
        Url url = getUrl(urlDto);
        urlRepository.save(url);
        urlCacheRepository.save(url);
    }

    private Url getUrl(UrlDto urlDto) {
        urlDto.setHash(hashCache.getHash().getHash());
        return urlMapper.toEntity(urlDto);
    }
}
