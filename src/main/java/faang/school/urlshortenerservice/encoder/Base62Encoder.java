package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SYMBOLS_NUMBER = BASE62_CHARACTERS.length();

    public List<String> encode(List<Long> numbers) {
        List<String> encodedList = new ArrayList<>();
        for (Long number : numbers) {
            encodedList.add(encodeSingle(number));
        }
        return encodedList;
    }

    private String encodeSingle(Long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % SYMBOLS_NUMBER);
            encoded.append(BASE62_CHARACTERS.charAt(remainder));
            number /= SYMBOLS_NUMBER;
        }
        return encoded.reverse().toString();
    }
}
