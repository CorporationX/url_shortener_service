package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BaseEncoder {

    @Value("${encode-base.alphabet}")
    private String alphabet;

    @Value("${encode-base.base:62}")
    private int base;

    public List<Hash> encode(List<Long> numbers) {
        List<Hash> hashes = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            hashes.add(encodeBase(number));
        }

        return hashes;
    }

    private Hash encodeBase(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            stringBuilder.append(alphabet.charAt((int) (number % base)));
            number /= base;
        }

        return Hash.builder()
                .hash(stringBuilder.reverse().toString())
                .build();
    }
}
