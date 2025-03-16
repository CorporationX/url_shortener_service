package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.model.Url;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlService {

    List<Url> getAndDeleteOldUrls(LocalDateTime olderThan);

    Url createUrl(String url);
}
