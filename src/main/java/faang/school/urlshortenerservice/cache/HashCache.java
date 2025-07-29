package faang.school.urlshortenerservice.cache;

import java.util.Set;

public interface HashCache {

    void put(Set<String> hashes);

    String get();

    long getCurrentSize();

    boolean isNotEnoughHashes();
}