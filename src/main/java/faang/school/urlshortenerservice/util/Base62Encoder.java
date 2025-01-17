package faang.school.urlshortenerservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    @Value("${base62.alphabet}")
    private String base62;
    @Value("${base62.length")
    private int base62length;

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            hashes.add(encodeToBase62(number));
        }
        return hashes;
    }

    private String encodeToBase62(Long number) {
        if (number == 0) {
            return String.valueOf(base62.charAt(0));
        }


        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % base62length);
            encoded.append(base62.charAt(remainder));
            number /= base62length;
        }
        return encoded.toString();
    }

}
