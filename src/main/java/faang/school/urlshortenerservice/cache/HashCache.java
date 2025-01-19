package faang.school.urlshortenerservice.cache;

import org.springframework.stereotype.Component;

@Component
public class HashCache {

    // заглушка
    public String getHash(String longUrl) {
        return "hash";
    }
}