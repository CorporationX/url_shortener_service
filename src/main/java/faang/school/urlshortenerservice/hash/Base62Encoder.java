package faang.school.urlshortenerservice.hash;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        List<String> encodedList = new ArrayList<>();
        numbers.forEach(number -> {
            StringBuilder result = new StringBuilder();
            while (number > 0) {
                int remainder = (int) (number % BASE);
                result.insert(0, BASE62_ALPHABET.charAt(remainder));
                number /= BASE;
            }
            encodedList.add(result.toString());
        });
        return encodedList;
    }
}
