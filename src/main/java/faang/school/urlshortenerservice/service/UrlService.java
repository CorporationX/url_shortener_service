package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    @Value("${URL.static_address}")
    private String staticAddress;

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    public String findUrl(String hash) {

    }


    public UrlDto convertUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        urlCacheRepository.saveAssociation(urlDto.getUrl(), hash);
        urlRepository.saveAssociation(urlDto.getUrl(), hash);
        UrlDto shortUrl = new UrlDto();
        shortUrl.setUrl(staticAddress.concat(hash));
        return shortUrl;
    }

}
