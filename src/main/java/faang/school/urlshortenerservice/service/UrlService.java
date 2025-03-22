package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

  private final HashCache hashCache;
  private final UrlRepository urlRepository;
  private final UrlCacheRepository urlCacheRepository;

  public String makeShortUrl(String longUrl){
    String hash = hashCache.getHash();
    urlRepository.saveUrl(new Url(hash, longUrl, LocalDateTime.now()));
    urlCacheRepository.saveUrl(hash, longUrl);

    return hash;
  }

  public String getLongUrl(String hash) {
    Optional<String> cachedUrl = urlCacheRepository.getUrl(hash);
    if (cachedUrl.isPresent()) {
      return cachedUrl.get();
    }

    Optional<String> dbUrl = urlRepository.findUrlByHash(hash);
    if (dbUrl.isPresent()) {
      urlCacheRepository.saveUrl(hash, dbUrl.get());
      return dbUrl.get();
    }

    throw new UrlNotFoundException("url-shortener", String.format("url with hash %s not found", hash));
  }
}
