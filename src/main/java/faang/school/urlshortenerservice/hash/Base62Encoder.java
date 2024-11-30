package faang.school.urlshortenerservice.hash;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final char[] alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::toBase62)
                .toList();
    }

    private String toBase62(Long num) {
        StringBuilder result = new StringBuilder();

        while (num > 0) {
            int index = (int) (num % 62);
            result.append(alphabet[index]);
            num /= 62;
        }

        return result.reverse().toString();
    }
}
