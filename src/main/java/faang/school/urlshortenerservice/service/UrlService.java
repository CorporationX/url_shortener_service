package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.UrlBaza;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    @Cacheable(cacheNames = "generateShortUrl", key = "#originalUrl")
    public void generateShortUrl(UrlDto originalUrl) {
        String firstElement = hashCache.getHash();
        if(firstElement == null){
            throw new RuntimeException("Please try again later");
        }
        UrlBaza urlBaza = UrlBaza.builder()
                .hash(firstElement)
                .url(originalUrl.getOriginalUrl())
                .build();
        urlRepository.save(urlBaza);
        log.info("Saved hash {}, and original url in baza",firstElement);
    }
    @Cacheable(cacheNames = "returnFullUrl", key = "#requesthash")
    public String returnFullUrl(String requesthash){
        UrlBaza urlBaza = urlRepository.findById(requesthash)
                .orElseThrow(() -> new IllegalStateException("For the hash the full URL not in the database"));
        return urlBaza.getUrl();
    }
}