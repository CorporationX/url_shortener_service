package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import java.util.List;
import java.util.Optional;

public interface UrlRepository {

  List<String> deleteExpiredUrls();

  void saveUrl(Url url);

  Optional<String> findUrlByHash(String hash);
}