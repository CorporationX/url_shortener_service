package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.model.Url;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlService {

    List<Url> getAndDeleteOldUrls(LocalDateTime olderThan);

    Url createUrl(String url);

    String getUrlByHash(String hash);

    String buildUrl(Url url, HttpServletRequest request);
}
