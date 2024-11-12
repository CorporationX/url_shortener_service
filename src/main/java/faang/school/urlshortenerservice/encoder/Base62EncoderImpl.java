package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class Base62EncoderImpl implements Base62Encoder {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    @Value("${spring.base62.number of characters in hashcode}")
    private int numberOfCharacters;

    @Override
    public Hash encodeSingle(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            sb.append(ALPHABET.charAt(remainder));
            number /= BASE;
        }

        sb.reverse();

        while (sb.length() < numberOfCharacters) {
            sb.insert(0, '0');
        }
        return Hash.builder()
                .hash(String.valueOf(sb))
                .build();
    }

    @Override
    public List<Hash> encode(List<Long> numbers) {
        List<Hash> encodedList = new ArrayList<>();
        for (Long number : numbers) {
            encodedList.add(encodeSingle(number));
        }
        return encodedList;
    }
}
