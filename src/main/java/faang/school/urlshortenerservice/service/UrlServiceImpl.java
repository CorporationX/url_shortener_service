package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.hash.HashPreGenerator;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashPreGenerator hashPreGenerator;
    private final UrlMapper urlMapper;

    @Value("${host.base-host}")
    private String host;

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
    @Transactional
    public String createAndSaveShortUrl(UrlDto urlDto) {
        Optional<Url> existUrl = urlRepository.findByUrl(urlDto.getUrl());
        if (existUrl.isPresent()) {
            Url dbUrl = existUrl.get();
            return createShortUrl(dbUrl.getHash());
        }
        String hash = hashPreGenerator.getHash();
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hash);
        urlRepository.save(url);
        urlCacheRepository.save(url.getUrl(), hash);
        return createShortUrl(hash);
    }

    private String createShortUrl(String hash) {
        return host + "/" + hash;
    }
}
