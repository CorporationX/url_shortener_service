package faang.school.urlshortenerservice.service.interfaces;

import faang.school.urlshortenerservice.dto.UrlRequest;

public interface UrlService {

    String createShortUrl(UrlRequest urlRequest);

    String getOriginalUrl(String hash);
}
