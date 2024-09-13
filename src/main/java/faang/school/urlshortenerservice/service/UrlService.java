package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    @Value("${URL.static_address}")
    private String staticAddress;

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    public UrlDto findUrl(String hash) {
        UrlDto urlDto = new UrlDto();
        Optional<Pair<String,String>> originalUrlPair = urlCacheRepository.getAssociation(hash);

        if (originalUrlPair.isPresent()) {
            urlDto.setUrl(originalUrlPair.get().getSecond());
        } else {
            urlDto.setUrl(urlRepository.findByHash(hash));
        }
        return urlDto;
    }


    public UrlDto convertToShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        urlCacheRepository.saveAssociation(urlDto.getUrl(), hash);
        urlRepository.saveAssociation(urlDto.getUrl(), hash);
        UrlDto shortUrl = new UrlDto();
        shortUrl.setUrl(staticAddress.concat(hash));
        return shortUrl;
    }
}
