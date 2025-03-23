package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARACTERS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final ThreadLocal<StringBuilder> stringBuilderThreadLocal =
            ThreadLocal.withInitial(StringBuilder::new);

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(Long number) {
        if (number == 0) {
            return "0";
        }
        StringBuilder result = stringBuilderThreadLocal.get();
        result.setLength(0);
        while (number > 0) {
            int remainder = (int) (number % BASE62_CHARACTERS.length());
            result.append(BASE62_CHARACTERS.charAt(remainder));
            number /= BASE62_CHARACTERS.length();
        }
        return result.reverse().toString();
    }
}