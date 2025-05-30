package urlshortenerservice.generator;

import org.springframework.stereotype.Component;

@Component
public class Base62Converter {
    private static final String BASE_62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String convertToBase62(long number) {
        if (number == 0) {
            return "0";
        }

        StringBuilder result = new StringBuilder();
        long num = number;

        while (num > 0) {
            int remainder = (int)(num % BASE_62_ALPHABET.length());
            result.append(BASE_62_ALPHABET.charAt(remainder));
            num /= BASE_62_ALPHABET.length();
        }

        return result.reverse().toString();
    }
}
