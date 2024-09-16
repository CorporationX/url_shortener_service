package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.Redirect;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final URLCacheRepository urlCacheRepository;

    @Transactional
    public String getShortUrl(UrlDto urlDto) {

        String hash = hashCache.getHash();

        Url url = Url.builder()
                .url(urlDto.getUrl())
                .hash(hash)
                .build();

        Url savedUrl = urlRepository.save(url);
        urlCacheRepository.saveUrl(savedUrl);

        return hash;
    }

    @Transactional(readOnly = true)
    public Redirect getUrl(@NotNull String hash) {
        String url = urlCacheRepository.getUrl(hash);
        if (Objects.isNull(url)) {
            url = urlRepository.findById(hash)
                    .orElseThrow(() -> new DataNotFoundException("Url not with hash " + hash + " not found")).getUrl();
        }
        return Redirect.builder().url(url).build();
    }
}
