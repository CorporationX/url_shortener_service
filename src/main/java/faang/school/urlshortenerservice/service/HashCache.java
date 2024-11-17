package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

@Component
public class HashCache {
    public String getHashForUrl(String url) {
        return Integer.toHexString(url.hashCode()).substring(0, 6);
    }
}