package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class Base62Encoder {
    private final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(number -> {
                    StringBuilder builder = new StringBuilder();
                    do {
                        builder.insert(0, BASE62.charAt((int) (number % BASE62.length())));
                        number /= BASE62.length();
                    } while (number > 0);
                    return new Hash(builder.toString());
                }).toList();
    }

}
