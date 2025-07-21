package faang.school.urlshortenerservice;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "abcdefghijklnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String encode(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
            number /= BASE_62_CHARACTERS.length();
        }
        return sb.reverse().toString();
    }
}
