package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;

public interface UrlService {

    String shortenUrl(UrlDto url);

    Url getOriginalUrl(String hash);

}
