package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(number -> {
                    StringBuilder builder = new StringBuilder();
                    while (number > 0) {
                        builder.append(BASE62.charAt((int) (number % BASE62.length())));
                        number /= BASE62.length();
                    }
                    return builder.toString();
                }).toList();
    }
}
