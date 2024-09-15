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
            urlDto.setUrl(originalUrlPair.get().getFirst());
            log.info("Got original url {} from redis", urlDto.getUrl());
        } else {
            urlDto.setUrl(urlRepository.findByHash(hash));
            log.info("Got original url {} from database", urlDto.getUrl());
        }
        return urlDto;
    }

    public UrlDto convertToShortUrl(UrlDto urlDto) {
        String existedHash = containsUrl(urlDto.getUrl());
        if(existedHash.isEmpty()) {
            String hash = hashCache.getHash();
            log.info("got hash {} from cache", hash);
            urlCacheRepository.saveAssociation(urlDto.getUrl(), hash);
            urlRepository.saveAssociation(urlDto.getUrl(), hash);
            UrlDto shortUrl = new UrlDto();
            shortUrl.setUrl(staticAddress.concat(hash));
            log.info("configured shortUrl is {}", shortUrl.getUrl());
            return shortUrl;
        } else {
            log.info("Found url in database {} so hash will not be generated", urlDto.getUrl());
            urlDto.setUrl(staticAddress.concat(existedHash));
            log.info("configured shortUrl is {}", urlDto.getUrl());
            return urlDto;
        }
    }

    private String containsUrl(String url) {
        Optional<String> hash = urlRepository.findByUrl(url);
        return hash.orElse("");
    }
}
