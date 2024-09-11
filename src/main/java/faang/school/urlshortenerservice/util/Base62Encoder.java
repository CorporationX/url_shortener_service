package faang.school.urlshortenerservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.sqids.Sqids;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final Sqids sqids;

    public Base62Encoder(@Value("${url.hash.length:6}") int hashLength) {
        this.sqids = Sqids.builder()
                .minLength(hashLength)
                .alphabet(ALPHABET)
                .build();
    }

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(Long num) {
        return sqids.encode(List.of(num));
    }

}
