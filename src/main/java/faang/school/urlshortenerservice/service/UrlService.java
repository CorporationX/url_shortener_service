package faang.school.urlshortenerservice.service;

import java.time.LocalDate;

public interface UrlService {

    String getLongUrl(String hash);

    String getShortUrl(String url);
    void cleaningOldHashes(LocalDate date);
}
