package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final String BASE_62_CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012344567890";

    public String base62Encoding(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
            number /= BASE_62_CHARACTERS.length();
        }
        return builder.toString();
    }
}
