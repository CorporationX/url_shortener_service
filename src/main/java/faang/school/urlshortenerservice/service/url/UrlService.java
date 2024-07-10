package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.UrlDto;

import java.net.URL;

public interface UrlService {

    UrlDto createUrlHash(URL url);

    String getUrlFromHash(String hash);
}
