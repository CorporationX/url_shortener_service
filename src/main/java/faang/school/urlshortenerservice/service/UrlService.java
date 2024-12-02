package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {

    String saveShortUrlAssociation(UrlDto dto);

    String getOriginalUrl(String hash);
}
