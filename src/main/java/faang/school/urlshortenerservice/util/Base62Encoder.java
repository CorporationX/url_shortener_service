package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final long BASE = CHARACTERS.length();

    public List<String> encode(List<Long> numbers) {
        List<String> results = new ArrayList<>();
        for (Long number : numbers) {
            StringBuilder result = new StringBuilder();
            do {
                int remainder = (int) (number % BASE);
                result.append(CHARACTERS.charAt(remainder));
                number /= BASE;
            } while (number > 0);
            results.add(result.reverse().toString());
        }
        return results;
    }
}
