package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import java.time.LocalDateTime;
import java.util.List;

public interface UrlCacheRepository {

    Url saveUrl(Url url);

    Url findUrlByHash(String hash);

    List<Url> pollExpires(LocalDateTime expiredDate);

    Url findUrl(String url);
}
