package faang.school.urlshortenerservice.generator.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private final static String BASE_62_CHARACTERS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        numbers.forEach(number -> {
            while (number > 0) {
                stringBuilder.append(BASE_62_CHARACTERS
                        .charAt((int) (number % BASE_62_CHARACTERS.length())));
                number /= BASE_62_CHARACTERS.length();
            }
            hashes.add(stringBuilder.toString());
            stringBuilder.setLength(0);
        });
        return hashes;
    }
}