package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        List<String> resultHash = new ArrayList<>(numbers.size());

        for (Long number : numbers) {
            StringBuilder encodedString = new StringBuilder();

            if (number == 0) {
                resultHash.add(String.valueOf(BASE62_ALPHABET.charAt(0)));
                continue;
            }

            Long currentNumber = number;
            while (currentNumber > 0) {
                int remainder = (int) (currentNumber % BASE);
                encodedString.append(BASE62_ALPHABET.charAt(remainder));
                currentNumber /= BASE;
            }
            resultHash.add(encodedString.reverse().toString());
        }
        return resultHash;
    }
}