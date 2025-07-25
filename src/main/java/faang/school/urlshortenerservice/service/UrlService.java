package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;

import java.util.Optional;

public interface UrlService {

    void deleteUrlOlderOneYearAndSaveByHash(int limit);
    int countUrlsOlder();
    String findUrlByHash(String hash);
    Url createUrl (Url url);
}
