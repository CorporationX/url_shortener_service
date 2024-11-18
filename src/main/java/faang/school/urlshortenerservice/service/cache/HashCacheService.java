package faang.school.urlshortenerservice.service.cache;

import java.util.List;

public interface HashCacheService {

    String getHash();

    void addHash(List<String> hashes);
}
