package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.HashDto;

public interface UrlService {

    HashDto save(String url);

    String get(String hash);

    void freeUnusedHash();
}
