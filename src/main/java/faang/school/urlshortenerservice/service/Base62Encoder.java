package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encodeSequence(List<Long> emptySequences) {
        return emptySequences.stream()
                .map(this::generateBase62Hash)
                .toList();
    }

    private String generateBase62Hash(long number) {
        StringBuilder hashBuilder = new StringBuilder();
        BigInteger base = BigInteger.valueOf(62);
        BigInteger value = BigInteger.valueOf(number);

        while (value.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divRem = value.divideAndRemainder(base);
            int remainder = divRem[1].intValue();
            hashBuilder.insert(0, BASE62_CHARS.charAt(remainder));
            value = divRem[0];
        }

        return hashBuilder.toString();
    }
}
