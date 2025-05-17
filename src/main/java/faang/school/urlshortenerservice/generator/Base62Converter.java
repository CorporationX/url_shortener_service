package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

@Component
public class Base62Converter {
    private static final String BASE_62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String convertToBase62(long number) {
        StringBuilder result = new StringBuilder();
        long num = number;
        while (num > 0) {
            result.append(BASE_62_ALPHABET.charAt((int) (num % BASE_62_ALPHABET.length())));
            num /= BASE_62_ALPHABET.length();
        }

        return result.toString();
    }
}
