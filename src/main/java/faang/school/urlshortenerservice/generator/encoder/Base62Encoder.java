package faang.school.urlshortenerservice.generator.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder implements Base62 {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int BASE = CHARACTERS.length();

    @Override
    public String encode(Long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(CHARACTERS.charAt((int) (number % BASE)));
            number /= BASE;
        }
        return sb.reverse().toString();
    }

    @Override
    public List<String> encodeCollection(List<Long> nums) {
        List<String> hashes = new ArrayList<>();

        nums.forEach(num -> {
            String uniqueHash = encode(num);
            hashes.add(uniqueHash);
        });

        return hashes;
    }
}
