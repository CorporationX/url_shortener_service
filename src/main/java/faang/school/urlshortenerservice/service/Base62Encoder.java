package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encodeSequence(List<Long> emptySequences) {
        return emptySequences.stream()
                .map(id -> Hash.builder()
                        .id(id)
                        .value(generateBase62Hash(id))
                        .build())
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
