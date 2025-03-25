package faang.school.urlshortenerservice.service.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder implements Encoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE_LENGTH = BASE62_CHARS.length();

    @Override
    public List<String> encodeList(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(long number) {
        if (number == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE_LENGTH);
            sb.append(BASE62_CHARS.charAt(remainder));
            number = number / BASE_LENGTH;
        }
        return sb.reverse().toString();
    }
}
