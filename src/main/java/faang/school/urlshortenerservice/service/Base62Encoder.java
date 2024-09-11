package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;
import org.sqids.Sqids;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int HASH_LENGTH = 6;

    private final Sqids sqids;

    public Base62Encoder() {
        var builder = Sqids.builder()
                .minLength(HASH_LENGTH)
                .alphabet(ALPHABET);
        this.sqids = builder.build();
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
