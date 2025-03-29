package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final char[] BASE62_CHARS =
            ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789")
            .toCharArray();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeLong)
                .toList();
    }

    public String encodeLong(Long number) {
        if (number == 0) return "A";
        StringBuilder base62 = new StringBuilder();
        while (number > 0) {
            base62.insert(0, BASE62_CHARS[(int)(number % 62)]);
            number /= 62;
        }
        return base62.toString();
    }
}
