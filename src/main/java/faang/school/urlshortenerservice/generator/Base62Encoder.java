package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String applyBase62Encoding(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE_62_CHARACTERS.length());
            builder.append(BASE_62_CHARACTERS.charAt(remainder));
            number /= BASE_62_CHARACTERS.length();
        }
        return builder.reverse().toString();
    }
}