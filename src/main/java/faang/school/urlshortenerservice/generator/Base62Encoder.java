package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    public static final String BASE_62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final int BASE = 62;

    public List<String> encode(List<Long> numbers) {
        return numbers
                .stream()
                .map(number -> {
                    StringBuilder stringBuilder = new StringBuilder(6);
                    do {
                        stringBuilder.insert(0, BASE_62.charAt((int) (number % BASE)));
                        number /= BASE;
                    } while (number > 0);
                    return stringBuilder.toString();
                })
                .toList();
    }
}
