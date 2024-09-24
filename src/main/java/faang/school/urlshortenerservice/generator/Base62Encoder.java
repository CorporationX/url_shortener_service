package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    @Value("${spring.hash.alphabet}")
    private String alphabet;
    @Value("${spring.hash.base}")
    private int base;
    @Value("${spring.hash.length}")
    private int hashLength;

    public List<Hash> encodeNums(List<Long> uniqueNumbers) {
        return uniqueNumbers.stream().map(this::generateHash).toList();
    }

    public Hash generateHash(long uniqueNumber) {
        StringBuilder sb = new StringBuilder();

        while (uniqueNumber > 0) {
            int remainder = (int) (uniqueNumber % base);
            sb.append(alphabet.charAt(remainder));
            uniqueNumber /= base;
        }

        String encoded = sb.reverse().toString();
        if (encoded.length() > hashLength) {
            encoded = encoded.substring(0, hashLength);
        }

        return new Hash(encoded);
    }
}
