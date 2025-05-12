package faang.school.urlshortenerservice.component;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    private static final String base62Symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        numbers.forEach(number -> {
            hashes.add(number.toString());
        });
        return hashes;
    }
}
