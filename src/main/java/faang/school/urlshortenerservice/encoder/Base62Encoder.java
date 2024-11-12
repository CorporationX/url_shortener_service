package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_CHARACTERS.length();

    public static String encode(long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            encoded.append(BASE62_CHARACTERS.charAt(remainder));
            number /= BASE;
        }
        return encoded.reverse().toString();
    }
}
