package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    public List<String> encode(List<Long> numbers) {

        char[] base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        List<String> hashes = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (Long number : numbers) {

            if (number == 0) {
                return List.of(String.valueOf(base62Chars[0]));
            }
            while (number > 0) {
                sb.insert(0, base62Chars[(int) (number % 62)]);
                number /= 62;
            }
            String hash = sb.toString();
            hashes.add(hash);
        }
        return hashes;
    }
}
