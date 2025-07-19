package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final List<Character> ALPHABET = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');
    private static final Long BASE = 62L;


    public List<Hash> encode(List<Long> numbers) {
        return numbers.parallelStream()
                .map(num -> {
                    StringBuilder builder = new StringBuilder();
                    do {
                        long remains = num % BASE;
                        builder.append(ALPHABET.get((int) remains));
                        num /= BASE;
                    } while (num > 0);
                    return builder.reverse().toString();
                })
                .map(hash -> Hash.builder().hash(hash).build())
                .toList();
    }
}
