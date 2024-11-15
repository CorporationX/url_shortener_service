package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class Base62Encoder {

    private static final String BASE_62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .map(Hash::new)
                .toList();
    }

    private String encodeNumber(long number) {
        log.info("start encodeNumber with number - {}", number);
        StringBuilder sb = new StringBuilder();

        while (number > 0) {
            sb.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
            number /= BASE_62_CHARACTERS.length();
        }

        log.info("finish encodeNumber with hash: {} for number - {}", sb, number);
        return sb.toString();
    }
}
