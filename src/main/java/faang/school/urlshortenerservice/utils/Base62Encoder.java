package faang.school.urlshortenerservice.utils;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE_LENGTH = BASE62.length();

    public String encode(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE_LENGTH);
            builder.append(BASE62.charAt(remainder));
            number /= BASE_LENGTH;
        }
        return builder.toString();
    }
}