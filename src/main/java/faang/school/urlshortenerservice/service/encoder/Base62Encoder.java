package faang.school.urlshortenerservice.service.encoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        log.info("Start encoding: {} numbers", numbers.size());
        List<String> result = numbers.stream()
                .map(this::encodeNumber)
                .toList();

        log.info("End encoding: {} hashes", result.size());
        return result;
    }

    private String encodeNumber(Long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            encoded.append(BASE62_ALPHABET.charAt((int) (number % BASE)));
            number /= BASE;
        }
        return encoded.reverse().toString();
    }
}
