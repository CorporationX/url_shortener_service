package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.EncoderProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62EncoderImpl implements Base62Encoder {
    private final EncoderProperties props;

    @Override
    public List<String> encode(List<Long> numbers) {
        int base = props.base();
        int length = props.length();
        long space = props.space();
        long multiplier = props.multiplier();
        char[] alphabet = props.alphabet().toCharArray();

        List<String> out = new ArrayList<>(numbers.size());

        for (long number : numbers) {
            long permuted = multiplyMod(number, multiplier, space);
            out.add(toBase62(permuted, base, length, alphabet));
        }
        return out;
    }

    private long multiplyMod(long a, long b, long m) {
        return BigInteger.valueOf(a)
                .multiply(BigInteger.valueOf(b))
                .mod(BigInteger.valueOf(m))
                .longValue();
    }

    private String toBase62(long n, int base, int length, char[] alphabet) {
        char[] buf = new char[length];
        for (int i = length - 1; i >= 0; i--) {
            buf[i] = alphabet[(int) (n % base)];
            n /= base;
        }
        return new String(buf);
    }
}
