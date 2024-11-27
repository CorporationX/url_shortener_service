package faang.school.urlshortenerservice.service.hash;

import org.springframework.stereotype.Component;
import java.math.BigInteger;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final BigInteger BASE = BigInteger.valueOf(62);

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(Long number) {
        BigInteger num = BigInteger.valueOf(number);
        if (num.equals(BigInteger.ZERO)) {
            return String.valueOf(ALPHABET.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        while (num.compareTo(BigInteger.ZERO) > 0) {
            int remainder = num.remainder(BASE).intValue();
            sb.insert(0, ALPHABET.charAt(remainder));
            num = num.divide(BASE);
        }

        return sb.toString();
    }
}
