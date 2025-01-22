package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.LocalCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final LocalCache localCache;
    private final UrlRepository urlRepository;

    @Transactional
    public UrlDto createShortUrl(UrlDto urlDto) {
        String hash = localCache.getHash();
        urlRepository.saveUrl(hash, urlDto.getUrl());

        return new UrlDto(hash);
    }

    public String getLongUrl(String hash) {
        return urlRepository.findByHash(hash);
    }
}
