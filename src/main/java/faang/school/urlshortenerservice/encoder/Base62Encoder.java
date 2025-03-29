package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE_62_SEQUENCE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(number -> {
                    var hashString = new StringBuilder();
                    int length = BASE_62_SEQUENCE.length();

                    while (number > 0) {
                        hashString.append(BASE_62_SEQUENCE.charAt((int) (number % length)));
                        number /= length;
                    }

                    return Hash.builder()
                            .hash(hashString.toString())
                            .build();
                }).toList();
    }
}
