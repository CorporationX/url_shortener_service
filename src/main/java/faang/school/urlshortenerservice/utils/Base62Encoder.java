package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.exceptions.IllegalHashLength;
import faang.school.urlshortenerservice.exceptions.IllegalIdForHash;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Base62Encoder {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    public String encode(long num, int requiredLength) {
        if (num < 0) {
            throw new IllegalIdForHash("Id provided for hash generation must be non-negative.");
        }
        if (requiredLength <= 0) {
            throw new IllegalHashLength("Hash required length must be positive.");
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
            throw new IllegalHashLength("Generated hash is too long for required length: " + requiredLength);
        }

        return encoded.toString();
    }
}