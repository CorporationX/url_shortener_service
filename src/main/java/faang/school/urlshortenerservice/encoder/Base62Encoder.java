package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder implements BaseEncoder {
    private final String BASE64_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    public String encode(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE64_CHARACTERS.charAt((int) (number % BASE64_CHARACTERS.length())));
            number /= BASE64_CHARACTERS.length();
        }
        return builder.toString();
    }
}

