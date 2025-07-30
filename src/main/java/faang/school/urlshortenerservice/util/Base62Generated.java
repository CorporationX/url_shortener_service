package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Generated {

    private static final String BASE_62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String generateHashBase62(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            stringBuilder.append(BASE_62.charAt((int) number % BASE_62.length()));
            number /= BASE_62.length();
        }
        return stringBuilder.toString();
    }

    public List<String> encodeBase62(List<Long> base62List) {
        return base62List.stream().map(this::generateHashBase62).toList();
    }
}
