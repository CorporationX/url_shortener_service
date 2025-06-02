package faang.school.urlshortenerservice.encoder;

import java.util.List;

public final class Base62Encoder {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62.length();
    private static final String ZERO_HASH = "0";
    private static final String NEGATIVE_VALUE_MESSAGE = "The encoder does not support negative numbers";

    public static String encodeNumber(long number) {
        checkValueForNegative(number);

        if (number == 0) {
            return ZERO_HASH;
        }

        StringBuilder hash = new StringBuilder();
        while (number > 0) {
            int remind = (int) (number % BASE);
            hash.append(BASE62.charAt(remind));
            number /= BASE;
        }

        return hash.reverse().toString();
    }

    public static List<String> encodeNumbers(List<Long> numbers) {
        return numbers.stream()
                .map(Base62Encoder::encodeNumber)
                .toList();
    }

    private static void checkValueForNegative(long number) {
        if (number < 0) {
            throw new IllegalArgumentException(NEGATIVE_VALUE_MESSAGE);
        }
    }
}
