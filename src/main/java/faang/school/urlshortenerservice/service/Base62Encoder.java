package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62.length();

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());

        for (Long number : numbers) {
            hashes.add(encodeNumber(number));
        }

        return hashes;
    }

    private String encodeNumber(Long number) {
        StringBuilder hash = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % BASE);
            hash.append(BASE62.charAt(remainder));
            number /= BASE;
        }

        return hash.reverse().toString();
    }
}
