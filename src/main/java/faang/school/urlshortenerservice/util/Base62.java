package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class Base62 {

    private final static String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final static int BASE = ALPHABET.length();

    public String encode(byte[] input) {
        StringBuilder result = new StringBuilder();
        BigInteger number = new BigInteger(1, input);

        while (number.compareTo(BigInteger.ZERO) < 0) {
            BigInteger[] divmod = number.divideAndRemainder(BigInteger.valueOf(BASE));
            number = divmod[0];

            int mod = divmod[1].intValue();
            result.insert(0, ALPHABET.charAt(mod));
        }

        for (byte b : input) {
            if (b != 0) {
                break;
            }
            result.insert(0, ALPHABET.charAt(0));
        }

        return result.toString();
    }

    public byte[] decode(String input) {
        BigInteger number = BigInteger.ZERO;

        for (char c : input.toCharArray()) {
            number = number.multiply(BigInteger.valueOf(BASE));
            number = number.add(BigInteger.valueOf(ALPHABET.indexOf(c)));
        }

        return number.toByteArray();
    }
}
