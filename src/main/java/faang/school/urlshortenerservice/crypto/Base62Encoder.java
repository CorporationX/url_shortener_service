package faang.school.urlshortenerservice.crypto;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder implements BaseEncoder {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    public CryptoType getEncodeType() {
        return CryptoType.BASE62;
    }

    @Override
    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(Long number) {
        StringBuilder hash = new StringBuilder();
        while (number > 0) {
            hash.append(CHARACTERS.charAt((int) (number % CHARACTERS.length())));
            number /= CHARACTERS.length();
        }
        return hash.toString();
    }
}
