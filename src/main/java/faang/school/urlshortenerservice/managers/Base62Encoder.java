package faang.school.urlshortenerservice.managers;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::applyBase62Encoding)
                .toList();
    }

    private String applyBase62Encoding(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE62_CHARACTERS.charAt((int) (number % BASE62_CHARACTERS.length())));
            number /= BASE62_CHARACTERS.length();
        }
        return builder.toString();
    }

}
