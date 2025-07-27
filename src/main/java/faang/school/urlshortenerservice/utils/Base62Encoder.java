package faang.school.urlshortenerservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Base62Encoder {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    public String encode(long num, int requiredLength) {
        if (num < 0) {
            throw new IllegalArgumentException("Number must be non-negative.");
        }
        if (requiredLength <= 0) {
            throw new IllegalArgumentException("Required length must be positive.");
        }

        StringBuilder sb = new StringBuilder();
        if (num == 0) {
            sb.append(ALPHABET.charAt(0));
        } else {
            while (num > 0) {
                sb.append(ALPHABET.charAt((int) (num % BASE)));
                num /= BASE;
            }
        }
        StringBuilder encoded = new StringBuilder(sb.reverse().toString());

        while (encoded.length() < requiredLength) {
            encoded.insert(0, ALPHABET.charAt(0));
        }

        if (encoded.length() > requiredLength) {
            throw new IllegalArgumentException("Generated hash is too long for required length " + requiredLength);
        }

        return encoded.toString();
    }
}