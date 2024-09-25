package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final String BASE_62_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String encode(long uniqueKey) {
        StringBuilder hash = new StringBuilder();
        while (uniqueKey > 0) {
            int remainder = (int) (uniqueKey % BASE_62_CHARACTERS.length());
            hash.append(BASE_62_CHARACTERS.charAt(remainder));
            uniqueKey /= BASE_62_CHARACTERS.length();
        }
        return hash.reverse().toString();
    }
}
