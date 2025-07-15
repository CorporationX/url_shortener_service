package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlShortenerDto;

public interface UrlShortenerService {
     String create(UrlShortenerDto urlShortenerDto);
     String findUrlByHash(String hash);
     void deleteCreatedAYearAgo();
}
