package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();

        for (Long number : numbers) {
            StringBuilder builder = new StringBuilder();
            while (number > 0) {
                builder.append(BASE62_ALPHABET.charAt(number.intValue() % BASE62_ALPHABET.length()));
                number /= BASE62_ALPHABET.length();
            }

            hashes.add(builder.reverse().toString());
        }
        return hashes;
    }
}