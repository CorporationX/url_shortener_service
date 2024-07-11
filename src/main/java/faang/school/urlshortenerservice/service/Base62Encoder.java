package faang.school.urlshortenerservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Base62Encoder {

    private final static int BASE = 62;
    private final static String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final static Long FACTOR = 1_000_000_000L;

    @Value("${url.hash.size}")
    private int hashSize;

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        for (Long number : numbers) {
            number *= FACTOR;
            StringBuilder sb = new StringBuilder();
            do {
                sb.insert(0, CHARACTERS.charAt((int) (number % BASE)));
                number /= BASE;
            } while (number > 0 && sb.length() < hashSize);
            hashes.add(sb.toString());
        }
        return hashes;
    }
}
