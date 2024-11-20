package faang.school.urlshortenerservice.util.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Setter
@RequiredArgsConstructor
public class Base62Encoder implements Encoder {

    @Value("${spring.hash.encoder.hash-size}")
    private int hashSize;

    @Value("${spring.hash.encoder.character-base-62}")
    private String characterBase62;

    @Override
    public Hash encode(long number) {
        StringBuilder encodedHash = new StringBuilder();

        while (number > 0) {
            int currentIndex = (int) (number % characterBase62.length());
            encodedHash.append(characterBase62.charAt(currentIndex));
            number /= characterBase62.length();
        }

        encodedHash.reverse();

        while (encodedHash.length() < hashSize) {
            encodedHash.insert(0, '0');
        }

        return Hash.builder()
                .hash(encodedHash.toString())
                .build();
    }


    @Override
    public List<Hash> encodeBatch(List<Long> numbers) {
        List<Hash> encodedList = new ArrayList<>();

        for (Long number : numbers) {
            encodedList.add(encode(number));
        }

        return encodedList;
    }
}
