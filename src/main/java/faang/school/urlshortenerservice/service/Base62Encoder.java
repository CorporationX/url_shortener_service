package faang.school.urlshortenerservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Base62Encoder {
    private static final String BASE62_CHARS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String encode(long num) {
        if (num == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        StringBuilder encoded = new StringBuilder();
        while (num > 0) {
            encoded.append(BASE62_CHARS.charAt((int)(num % 62)));
            num /= 62;
        }

        return encoded.reverse().toString();
    }

    public List<String> encodeBatch(List<Long> numbers) {
        List<String> hashes = numbers.stream()
                .filter(Objects::nonNull)
                .map(this::encode)
                .map(x -> x.length() > 6 ? x.substring(0, 6) : x)
                .map(x -> x.length() < 6 ? String.format("%6s", x).replace(' ', '0') : x)
                .collect(Collectors.toList());
        return hashes;
    }
}
