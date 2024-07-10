package faang.school.urlshortenerservice.service.url;

import java.net.URL;

public interface UrlService {

    void createUrlHash(URL url);

    String getUrlFromHash(String hash);
}
