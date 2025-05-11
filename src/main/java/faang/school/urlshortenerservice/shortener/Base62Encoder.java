package faang.school.urlshortenerservice.shortener;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    public List<String> encode(List<Long> numbers) {
        List<String> result = new ArrayList<>(numbers.size());

        for (Long number : numbers) {
            result.add(encodeSingle(number));
        }

        return result;
    }

    private String encodeSingle(Long number) {
        if (number == 0) {
            return "0";
        }

        StringBuilder encoded = new StringBuilder();

        while (number > 0) {
            int remainder = (int)(number % BASE);
            encoded.append(BASE62_ALPHABET.charAt(remainder));
            number = number / BASE;
        }

        return encoded.reverse().toString();
    }
}
