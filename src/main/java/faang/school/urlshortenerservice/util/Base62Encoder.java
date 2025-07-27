package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<String> encodeBatch(List<Long> uniqueNumbers, int length) {
        return uniqueNumbers.stream().map((number) -> encode(number, length)).toList();
    }

    public static String encode(long number, int length) {
        StringBuilder sb = new StringBuilder();

        do {
            int reminder = (int) (number % BASE);
            sb.append(BASE62_ALPHABET.charAt(reminder));
            number /= BASE;
        } while (number > 0);

        while (sb.length() < length) {
            sb.append('0');
        }

        return sb.reverse().toString();
    }
}
