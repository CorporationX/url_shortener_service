package faang.school.urlshortenerservice.utils.encoder;

import faang.school.urlshortenerservice.dto.hash.HashDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE_62_LENGTH = BASE_62_CHARACTERS.length();

    public List<HashDto> encodeList(List<Long> number) {
        return number.stream().map(this::encode).toList();
    }

    public HashDto encode(Long number) {
        if (number < 0) {
            return new HashDto(number.toString());
        }

        StringBuilder base62 = new StringBuilder();
        while (number > 0) {
            base62.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_LENGTH)));
            number /= BASE_62_LENGTH;
        }

        String hash = base62.reverse().toString();
        String sixDigitHash = String.format("%6s", hash).replace(' ', '0');

        return new HashDto(sixDigitHash);
    }
}
