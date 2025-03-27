package faang.school.urlshortenerservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseEncoder {

    @Value("${encoder.base}")
    private int BASE;

    @Value("${encoder.characters}")
    private String CHARACTERS;

    @Value("${encoder.hashSize}")
    private int HASH_SIZE;

    public List<String> encodeBatch(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(long number) {
        StringBuilder encodedHash = new StringBuilder();
        do {
            encodedHash.insert(0, CHARACTERS.charAt((int) (number % BASE)));
            number /= BASE;
        } while (number > 0 || encodedHash.length() < HASH_SIZE);
        return encodedHash.toString();
    }

}
