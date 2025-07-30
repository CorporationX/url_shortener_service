package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.entity.Url;

import java.util.List;

public interface UrlService {

    List<Url> findExpiredUrl();

    String createShortUrl(UrlRequestDto urlRequest);

    String findUrlByHash(String hash);

    int countOldUrl();
}
