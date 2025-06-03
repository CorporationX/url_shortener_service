package faang.school.urlshortenerservice.service.interfaces;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.dto.UrlResponse;

public interface UrlService {

    UrlResponse createShortUrl(UrlRequest urlRequest);

    String getOriginalUrl(String hash);
}
