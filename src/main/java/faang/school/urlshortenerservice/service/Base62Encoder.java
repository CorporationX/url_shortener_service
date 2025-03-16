package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARS =
            "ABCDEFGHIJKLMNOPQR" +
            "STUVWXYZabcdefghijklm" +
            "nopqrstuvwxyz0123456789";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeLong)
                .toList();
    }

    public String encodeLong(Long number) {
        StringBuilder base62 = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % 62);
            base62.insert(0, BASE62_CHARS.charAt(remainder));
            number /= 62;
        }
        return base62.toString();
    }
}
