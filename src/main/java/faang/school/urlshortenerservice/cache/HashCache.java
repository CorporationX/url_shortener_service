package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

@Component
public class HashCache {
    public Hash getHashCache() {
        return new Hash();
    }
}