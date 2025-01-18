package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
   private final UrlCacheRepository urlCacheRepository;
   private final UrlRepository urlRepository;
   private final HashCache hashCache;

    public String findUrl(String hash) {
        String url = urlCacheRepository.findUrlFromCache(hash);

        if (url == null) {
            Url entity = urlRepository.findById(hash).orElseThrow(
                    () -> new UrlNotFoundException(String.format("URL with hash %s not found", hash)));
            url = entity.getUrl();
        }

        return url;
    }

   @Transactional
    public String saveNewHash(UrlDto urlDto) {
       Hash newHash = hashCache.getHash();
       Url newUrl = Url.builder()
               .hash(newHash.getHash())
               .url(urlDto.url())
               .build();

       saveToDataBase(newUrl);
       urlCacheRepository.saveToCache(newUrl);

       return newHash.getHash();
   }

   public void saveToDataBase(Url url) {
       urlRepository.save(url);
   }
}
