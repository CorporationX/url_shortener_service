package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.entity.Url;

public interface UrlShortenerService {

    String shrinkUrl(Url urlEntity);

    String getOriginalUrl(String hash);
}
