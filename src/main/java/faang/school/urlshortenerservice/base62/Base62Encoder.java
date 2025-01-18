package faang.school.urlshortenerservice.base62;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    @Value("${alphabet}")
    private String alphabetBase62;

    public List<Hash> encode(List<Long> nums) {
        return nums.stream().map(this::applyBase62Encoder).toList();
    }

    private Hash applyBase62Encoder(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        int base = alphabetBase62.length();
        while (number > 0) {
            stringBuilder.append(alphabetBase62.charAt((int) (number % base)));
            number /= base;
        }

        return new Hash(stringBuilder.reverse().toString());
    }
}
