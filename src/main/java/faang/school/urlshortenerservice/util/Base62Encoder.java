package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private final String
            BASE62_SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghigklmnopqrstuvwxyz" +
            "0123456789";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(this::getEncode).toList();
    }

    protected String getEncode(long number) {
        StringBuilder builder = new StringBuilder(1);
        do {
            builder.insert(0, BASE62_SYMBOLS.charAt((int) (number % BASE62_SYMBOLS.length())));
            number /= BASE62_SYMBOLS.length();
        } while (number > 0);
        return builder.toString();
    }
}
