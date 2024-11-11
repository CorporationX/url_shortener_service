package faang.school.urlshortenerservice.config.cache;

import org.springframework.stereotype.Component;

@Component
public class HashCache {
    public String getHash(String url) {
        return "mocked_hash_for_" + url;
    }
}
