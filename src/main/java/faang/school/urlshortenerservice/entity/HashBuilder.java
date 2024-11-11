package faang.school.urlshortenerservice.entity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HashBuilder {
    public static Hash build(String hash) {
        return Hash.builder()
                .hash(hash)
                .build();
    }

    public static Hash build(Long id, String hash) {
        return Hash.builder()
                .id(id)
                .hash(hash)
                .build();
    }
}
