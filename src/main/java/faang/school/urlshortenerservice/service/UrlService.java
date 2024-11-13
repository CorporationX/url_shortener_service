package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.response.UrlResponse;

public interface UrlService {

    String getUrl(String hash);

    UrlResponse save(UrlDto urlDto);
}
