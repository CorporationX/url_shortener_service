package baum.urlshortenerservice.service;

import baum.urlshortenerservice.dto.UrlDto;

public interface UrlService {

    String saveShortUrlAssociation(UrlDto dto);

    String getOriginalUrl(String hash);
}
