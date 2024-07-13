package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder implements Encoder<Long> {
    private final String characters;
    private final int divider;

    public Base62Encoder(@Value("${encoder.base.characters}") String characters,
                         @Value("#{new Integer('${encoder.base.divider}')}") int divider) {
        this.characters = characters;
        this.divider = divider;
    }

    @Override
    public List<Hash> encode(List<Long> keys) {
        return keys.stream()
                .map(this::encodeSingleNumber)
                .toList();
    }

    private Hash encodeSingleNumber(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        do {
            stringBuilder.append(characters.charAt((int) number % divider));
            number /= divider;
        } while (number > 0);
        return new Hash(stringBuilder.toString());
    }
}