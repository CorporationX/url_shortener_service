package faang.school.urlshortenerservice.cache.hash;

import java.util.List;

public interface HashCache {

    String pop();

    void putAll(List<String> hashes);
}
