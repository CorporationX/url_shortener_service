package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
   private final UrlCacheRepository urlCacheRepository;
   private final UrlRepository urlRepository;

   public String findUrl(String hash) {
       String cachedUrl = urlCacheRepository.findUrlFromCache(hash);

       if (cachedUrl != null) {
           return cachedUrl;
       }

       Url url = urlRepository.findById(hash).orElseThrow(() -> new UrlNotFoundException(String.format("Url with hash %s not found", hash)));
       urlCacheRepository.saveToCache(hash, url.getUrl());
       return url.getUrl();
   }
}
