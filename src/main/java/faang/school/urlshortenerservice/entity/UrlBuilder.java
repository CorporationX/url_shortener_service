package faang.school.urlshortenerservice.entity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UrlBuilder {
    public static Url build(String hash, String longUrl) {
        return Url.builder()
                .hash(hash)
                .longUrl(longUrl)
                .build();
    }
}
