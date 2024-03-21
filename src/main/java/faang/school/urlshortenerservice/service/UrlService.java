package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlCash;
import faang.school.urlshortenerservice.repository.UrlCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlCashRepository urlCashRepository;
    private final HashCache hashCache;
    private  final UrlRepository urlRepository;

    private static final String URL_PATTERN = "https://corpX.com/";

    public UrlDto createShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        String shortUrl = URL_PATTERN + hash;
        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl()).build();
        urlRepository.save(url);
        urlCashRepository.save(new UrlCash(hash,urlDto.getUrl()));

        return new UrlDto(shortUrl);
    }

}