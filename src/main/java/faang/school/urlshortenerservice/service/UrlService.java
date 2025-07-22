package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.OriginalUrl;

public interface UrlService {
    String shorten(OriginalUrl originalUrl);
    String getOriginal(String hash);
}
