package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;

public interface UrlService {

    String getLongUrlByHash(String hash);

    String getShortUrlByHash(String url);
}
