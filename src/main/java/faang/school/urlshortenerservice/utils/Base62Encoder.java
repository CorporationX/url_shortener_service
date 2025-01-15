package faang.school.urlshortenerservice.utils;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final String BASE_62_ELEMENTS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(this::base62Encoding).toList();
    }

    private String base62Encoding(long number) {
        StringBuilder builder = new StringBuilder();

        while (number > 0) {
            builder.append(BASE_62_ELEMENTS.charAt((int) number % BASE_62_ELEMENTS.length()));
            number /= 62;
        }

        return builder.toString();
    }
 }
