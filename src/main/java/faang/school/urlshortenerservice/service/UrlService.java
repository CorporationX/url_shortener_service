package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;

public interface UrlService {

    String getLongUrlByHash(Hash hash);
}
