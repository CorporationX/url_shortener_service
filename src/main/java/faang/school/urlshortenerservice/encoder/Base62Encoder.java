package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int BASE = BASE62_CHARACTERS.length();

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
            int remainder = (int) (number % BASE);
            encoded.append(BASE62_CHARACTERS.charAt(remainder));
            number /= BASE;
        }
        return encoded.reverse().toString();
    }
}
