package faang.school.urlshortenerservice.config.cache;

import org.springframework.stereotype.Component;

@Component
public class HashCache {
    private static int i = 0;

    public String getHash(String url) {
        return "abc" + i++;
    }
}
