package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.generator.HashCache;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
  private final HashCache hashCache;
  private final UrlRepository urlRepository;
  private final UrlCacheService urlCacheService;
  private final MessageSource messageSource;

  public String getHash(UrlDto urlDto) {
    String hash = hashCache.getHash();
    String long_url = urlDto.url();
    Url urlEntity = new Url(hash, long_url);
    urlRepository.save(urlEntity);
    urlCacheService.cacheLongUrl(hash, long_url);
    return hash;
  }

  public String getLongUrl(String hash) {
    String longUrl = urlCacheService.getCachedLongUrl(hash);
    return Optional.ofNullable(longUrl)
            .orElseGet(() -> urlRepository.findByHash(hash)
                    .map(Url::getUrl)
                    .orElseThrow(() -> new EntityNotFoundException(
                            messageSource.getMessage("exception.entity_not_found", null, Locale.getDefault())
                    )));
  }
}
