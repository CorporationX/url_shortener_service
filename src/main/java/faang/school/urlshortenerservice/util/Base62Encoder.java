package faang.school.urlshortenerservice.util;

public final class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ALPHABET_LENGTH = BASE62_ALPHABET.length();

    public static String encode(long value) {
        StringBuilder encoded = new StringBuilder();
        while (value > 0) {
            int remainder = (int) (value % ALPHABET_LENGTH);
            encoded.insert(0, BASE62_ALPHABET.charAt(remainder));
            value /= ALPHABET_LENGTH;
        }
        return encoded.toString();
    }
}