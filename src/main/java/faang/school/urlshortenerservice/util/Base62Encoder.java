package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String encode(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % BASE62_CHARACTERS.length());
            sb.append(BASE62_CHARACTERS.charAt(remainder));
            num /= BASE62_CHARACTERS.length();
        }

        return sb.toString();
    }
}
