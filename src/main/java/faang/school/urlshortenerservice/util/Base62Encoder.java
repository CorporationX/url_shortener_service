package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String encode(long number) {
        StringBuilder base62 = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % 62);
            base62.insert(0, BASE62_CHARS.charAt(remainder));
            number /= 62;
        }
        return base62.toString();
    }

    public Stream<String> encode(List<Long> number) {
        return number.stream().map(this::encode);
    }
}