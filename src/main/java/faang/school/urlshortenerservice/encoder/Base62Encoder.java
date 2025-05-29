package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62.length();

    public String encodeNumber(long number) {
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
                .map(this::encodeNumber)
                .toList();
    }
}
