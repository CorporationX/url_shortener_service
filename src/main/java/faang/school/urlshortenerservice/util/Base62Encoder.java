package faang.school.urlshortenerservice.util;

public final class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encode(long value) {
        StringBuilder encoded = new StringBuilder();
        while (value > 0) {
            int remainder = (int) (value % 62);
            encoded.insert(0, BASE62_ALPHABET.charAt(remainder));
            value /= 62;
        }
        return encoded.toString();
    }
}