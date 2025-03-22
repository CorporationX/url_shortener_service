package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import java.time.LocalDateTime;
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
}
