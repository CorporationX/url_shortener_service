package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    @Value("${shortener.host}")
    private String host;

    @Override
    public UrlResponse generateShortUrl(UrlDto urlDto) {
        Optional<Url> url = urlRepository.findByUrl(urlDto.url());
        if (url.isPresent()) {
            return new UrlResponse(generateShortUrl(url.get().getHash()));
        }
        String hash = hashCache.getHash().join();
        Url newUrl = urlMapper.toUrl(urlDto);
        newUrl.setHash(hash);
        urlRepository.save(newUrl);
        return new UrlResponse(generateShortUrl(hash));
    }

    @Override
    public UrlResponse getUrl(String shortUrl) {
        return null;
    }

    private String generateShortUrl(String hash) {
        return "%s/%s".formatted(host, hash);
    }
}
