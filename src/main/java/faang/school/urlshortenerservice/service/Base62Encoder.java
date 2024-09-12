package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class Base62Encoder {

    private final int base = 62;
    private final static String STRING_62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        StringBuilder sb = new StringBuilder();

        List<String> hashes = numbers.stream()
                .map(number -> {
                    while (number > 0) {
                        sb.insert(0, STRING_62.charAt((int) (number % base)));
                        number /= base;
                    }
                    return sb.toString();
                })
                .toList();
        return hashes;
    }
}
