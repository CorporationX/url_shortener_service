package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;

public interface UrlCacheRepository {
    public void save(String hash, Url url);
    public Url get(String hash);
}