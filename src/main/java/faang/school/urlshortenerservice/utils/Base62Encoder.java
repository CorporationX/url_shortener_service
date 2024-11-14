package faang.school.urlshortenerservice.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    private final static String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final static int BASE62_LENGTH = BASE62_CHARS.length();

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        StringBuilder encoded = new StringBuilder();

        for (Long num : numbers) {
            while (num > 0 && encoded.length() < 6) {
                int remainder = (int) (num % BASE62_LENGTH);
                encoded.append(BASE62_CHARS.charAt(remainder));
                num /= BASE62_LENGTH;
            }
            encoded.reverse().toString();
            hashes.add(String.valueOf(encoded));
            encoded.setLength(0);
        }

        return hashes;
    }

}
