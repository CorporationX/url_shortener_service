package faang.school.urlshortenerservice.shortener;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    public String encodeSingle(long number) {
        StringBuilder sb = new StringBuilder();
        do {
            int index = (int) (number % BASE);
            sb.append(BASE62_ALPHABET.charAt(index));
            number /= BASE;
        } while (number > 0);
        return sb.toString();
    }

    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(this::encodeSingle).toList();
    }
}
