package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream().map(number -> {
            StringBuilder hash = new StringBuilder();
            while (number > 0) {
                hash.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
                number /= BASE_62_CHARACTERS.length();
            }
            return new Hash(hash.reverse().toString());
        }).toList();

    }
}
