package faang.school.urlshortenerservice.repository;

import java.util.Optional;

public interface UrlCacheRepository {

  void saveUrl(String hash, String longUrl);

  Optional<String> getUrl(String hash);
}