package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class Base62EncoderImpl implements Encoder {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ALPHABET_SIZE = ALPHABET.length();

    @Value("${spring.base62.number of characters in hashcode}")
    private int numberOfCharacters;

    @Override
    public Hash encodeSingle(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % ALPHABET_SIZE);
            stringBuilder.append(ALPHABET.charAt(remainder));
            number /= ALPHABET_SIZE;
        }

        stringBuilder.reverse();

        while (stringBuilder.length() < numberOfCharacters) {
            stringBuilder.insert(0, '0');
        }
        return Hash.builder()
                .hash(String.valueOf(stringBuilder))
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
