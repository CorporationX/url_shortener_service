package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;

    @Override
    public String getOriginalUrl(String hash) {
        String cachedUrl = urlCacheRepository.getUrlByHash(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }
        Url url = urlRepository.findByHash(hash)
                .orElseThrow(() -> new UrlNotFoundException("Url not found for hash: " + hash));
        urlCacheRepository.save(url.getUrl(), hash);
        return url.getUrl();
    }

    @Override
    public String createAndSaveShortUrl(UrlDto urlDto) {
        Optional<Url> existUrl = urlRepository.findByUrl(urlDto.getUrl());
        if (existUrl.isPresent()) {
            Url dbUrl = existUrl.get();
            return createShortUrl(dbUrl.getUrl(), dbUrl.getHash());
        }
        String hash = hashCache.getHash();
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hash);
        urlRepository.save(url);
        urlCacheRepository.save(url.getUrl(), hash);
        return createShortUrl(url.getUrl(), hash);
    }

    private String createShortUrl(String originalUrl, String hash) {
        try {
            URI uri = new URI(originalUrl);
            return uri.getScheme() + "://" + uri.getHost() + "/" + hash;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + originalUrl, e);
        }
    }
}
