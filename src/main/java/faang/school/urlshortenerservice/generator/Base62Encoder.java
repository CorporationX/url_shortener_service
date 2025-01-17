package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final String base62Characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public List<String> encode(List<Long> numbers) {
         return numbers.stream()
                .map(this::applyBase62Encoding)
                .toList();
    }

    private String applyBase62Encoding(long number) {
        StringBuilder builder = new StringBuilder();

        while (number > 0) {
            builder.append(base62Characters.charAt((int) (number % base62Characters.length())));
            number /= base62Characters.length();
        }

        return builder.toString();
    }
}
