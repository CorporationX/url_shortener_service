package faang.school.urlshortenerservice.entity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HashBuilder {
    public static Hash build(String hash) {
        return Hash.builder()
                .hash(hash)
                .build();
    }
}
