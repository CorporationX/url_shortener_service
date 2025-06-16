package faang.school.urlshortenerservice.encoder;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Base62Encoder {

    private final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int BASE = BASE62.length();
    private final String ZERO_HASH = "0";
    private final String NEGATIVE_VALUE_MESSAGE = "The encoder does not support negative numbers";

    public String encodeNumber(long number) {
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

    public List<String> encodeNumbers(List<Long> numbers) {
        return numbers.stream()
                .map(Base62Encoder::encodeNumber)
                .toList();
    }

    private void checkValueForNegative(long number) {
        if (number < 0) {
            throw new IllegalArgumentException(NEGATIVE_VALUE_MESSAGE);
        }
    }
}
