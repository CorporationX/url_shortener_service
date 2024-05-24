package faang.school.urlshortenerservice.service.encoder;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private final static String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String encode(long num) {
        StringBuilder result = new StringBuilder();
        while (num > 0) {
            int digit = (int) (num % BASE_62_CHARACTERS.length());
            result.append(BASE_62_CHARACTERS.charAt(digit));
            num /= BASE_62_CHARACTERS.length();
        }
        return result.toString();
    }
}
