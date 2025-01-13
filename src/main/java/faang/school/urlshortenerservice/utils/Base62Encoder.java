package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_CHARSET.length();

    public List<HashEntity> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeBase62)
                .map(HashEntity::new)
                .collect(Collectors.toList());
    }

    private String encodeBase62(Long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            encoded.insert(0, BASE62_CHARSET.charAt((int) (number % BASE)));
            number /= BASE;
        }
        return encoded.toString();
    }
}
