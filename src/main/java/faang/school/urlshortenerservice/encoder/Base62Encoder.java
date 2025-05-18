package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = CHARACTERS.length();

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            hashes.add(toBase62(number));
        }
        return hashes;
    }

    private String toBase62(long number) {
        if (number == 0) {
            return String.valueOf(CHARACTERS.charAt(0));
        }

        StringBuilder strBuild = new StringBuilder();
        while (number > 0) {
            int digit = (int) (number % BASE);
            strBuild.append(CHARACTERS.charAt(digit));
            number /= BASE;
        }
        return strBuild.reverse().toString();
    }
}
