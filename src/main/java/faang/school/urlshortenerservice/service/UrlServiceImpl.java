package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.UrlServiceProperties;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.LocalCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final LocalCache localCache;
    private final UrlServiceProperties properties;

    @Override
    @Transactional
    public ShortUrlDto createShortUrl(UrlDto urlDto) {
        validateUrl(urlDto.url());
        String hash = localCache.getHash();
        urlRepository.save(new Url(hash, urlDto.url(), LocalDateTime.now()));
        //ToDo: save to redis cache
        return new ShortUrlDto(properties.getBaseShortUrl() + hash);
    }

    @Override
    public UrlDto getUrl(String hash) {
        //ToDo: find in redis cache
        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Url for hash %s not found", hash)));
        return new UrlDto(url.getUrl());
    }

    private void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("url can not be null or blank");
        }
        try {
            new URI(url).toURL();
        } catch (URISyntaxException | IllegalArgumentException | MalformedURLException e) {
            throw new IllegalArgumentException(String.format("%s is not valid url", url));
        }
    }
}

